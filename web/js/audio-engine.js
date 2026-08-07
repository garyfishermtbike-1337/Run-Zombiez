// Layered audio engine: one HTMLAudioElement + GainNode + StereoPannerNode per
// channel (MUSIC / VOICE / SFX / AMBIENCE), so tracks can play simultaneously
// and cross-fade independently. Web Audio's native StereoPannerNode gives us
// real spatial pan for zombie encounters for free — no custom DSP needed.

const CHANNELS = ["MUSIC", "VOICE", "SFX", "AMBIENCE"];
const FADE_STEP_MS = 30;

export class AudioEngine {
  constructor(basePath = "audio/") {
    this.basePath = basePath;
    this.ctx = null;
    this.nodes = {};
    this.fadeTimers = {};
  }

  // AudioContext must be created/resumed from a user gesture (browser autoplay policy).
  ensureContext() {
    if (this.ctx) return;
    this.ctx = new (window.AudioContext || window.webkitAudioContext)();
    for (const channel of CHANNELS) {
      const element = new Audio();
      element.crossOrigin = "anonymous";
      element.preload = "auto";
      const source = this.ctx.createMediaElementSource(element);
      const gain = this.ctx.createGain();
      const panner = this.ctx.createStereoPanner();
      gain.gain.value = 0;
      source.connect(gain).connect(panner).connect(this.ctx.destination);
      this.nodes[channel] = { element, gain, panner };
    }
  }

  async resume() {
    this.ensureContext();
    if (this.ctx.state === "suspended") await this.ctx.resume();
  }

  play(channel, assetPath, { loop = false, fadeInMs = 400, volume = 1, pan = 0 } = {}) {
    this.ensureContext();
    const node = this.nodes[channel];
    node.panner.pan.value = Math.max(-1, Math.min(1, pan));
    node.element.loop = loop;
    node.element.src = this.basePath + assetPath;
    node.element.currentTime = 0;
    node.element.play().catch((err) => {
      // Missing/unresolvable audio assets are expected until real audio is
      // produced (see docs/ASSETS.md) — log and keep the mission running silently.
      console.warn(`[AudioEngine] ${channel} could not play "${assetPath}":`, err.message);
    });
    this.fadeTo(channel, volume, fadeInMs);
  }

  stop(channel, fadeOutMs = 400) {
    this.fadeTo(channel, 0, fadeOutMs, () => {
      const node = this.nodes[channel];
      if (node) node.element.pause();
    });
  }

  stopAll(fadeOutMs = 500) {
    CHANNELS.forEach((c) => this.stop(c, fadeOutMs));
  }

  duck(channel, to = 0.25, durationMs = 250) {
    this.fadeTo(channel, to, durationMs);
  }

  restore(channel, to = 1, durationMs = 250) {
    this.fadeTo(channel, to, durationMs);
  }

  fadeTo(channel, target, durationMs, onComplete) {
    const node = this.nodes[channel];
    if (!node) return;
    clearInterval(this.fadeTimers[channel]);

    const start = node.gain.gain.value;
    if (durationMs <= 0) {
      node.gain.gain.value = target;
      onComplete?.();
      return;
    }
    const steps = Math.max(1, Math.round(durationMs / FADE_STEP_MS));
    let i = 0;
    this.fadeTimers[channel] = setInterval(() => {
      i += 1;
      node.gain.gain.value = start + (target - start) * (i / steps);
      if (i >= steps) {
        clearInterval(this.fadeTimers[channel]);
        onComplete?.();
      }
    }, FADE_STEP_MS);
  }

  release() {
    CHANNELS.forEach((c) => clearInterval(this.fadeTimers[c]));
    this.ctx?.close();
  }
}
