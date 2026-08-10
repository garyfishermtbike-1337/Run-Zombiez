// Drives a mission's JSON timeline. Deliberately timeline-driven, not
// GPS-driven, for the MVP (events fire at fixed atSeconds offsets) — GPS can
// hook into the same dispatch() path as a future extension.

export class MissionEngine {
  constructor(audioEngine) {
    this.audioEngine = audioEngine;
    this.listeners = new Set();
    this.state = this.initialState();
    this._running = false;
    this._duckRestoreTimer = null;
  }

  initialState() {
    return {
      mission: null,
      isRunning: false,
      isComplete: false,
      elapsedSeconds: 0,
      currentText: null,
      currentSpeaker: null,
      currentArtwork: null
    };
  }

  onChange(fn) {
    this.listeners.add(fn);
    return () => this.listeners.delete(fn);
  }

  setState(patch) {
    this.state = { ...this.state, ...patch };
    this.listeners.forEach((fn) => fn(this.state));
  }

  async start(mission) {
    this.stop();
    this._running = true;
    this.setState({ ...this.initialState(), mission, isRunning: true });

    const events = [...mission.timeline].sort((a, b) => a.atSeconds - b.atSeconds);
    let lastSecond = 0;
    for (const event of events) {
      if (!this._running) return;
      const waitMs = Math.max(0, (event.atSeconds - lastSecond) * 1000);
      await this.delay(waitMs);
      if (!this._running) return;
      lastSecond = event.atSeconds;
      this.dispatch(event);
    }
    if (!this._running) return;
    const remainingMs = Math.max(0, (mission.durationSeconds - lastSecond) * 1000);
    await this.delay(remainingMs);
    if (!this._running) return;
    this.complete();
  }

  delay(ms) {
    return new Promise((resolve) => {
      this._pendingTimeout = setTimeout(resolve, ms);
    });
  }

  dispatch(event) {
    const isDialogue = event.type === "HAVEN_TRANSMISSION" || event.type === "SURVIVOR_DIALOGUE";
    this.setState({
      elapsedSeconds: event.atSeconds,
      currentText: event.text ?? this.state.currentText,
      // Only dialogue events have a speaker — clear it for narration (zombie
      // encounters, story beats) instead of leaving a stale name on screen.
      currentSpeaker: isDialogue ? event.speaker : null,
      currentArtwork: event.artwork ?? this.state.currentArtwork
    });

    switch (event.type) {
      case "MUSIC_CUE":
        if (event.audioAsset) {
          this.audioEngine.play("MUSIC", event.audioAsset, {
            loop: event.loop ?? true,
            volume: event.volume ?? 0.55
          });
        }
        break;

      case "HAVEN_TRANSMISSION":
      case "SURVIVOR_DIALOGUE":
        this.audioEngine.duck("MUSIC");
        this.audioEngine.duck("AMBIENCE");
        if (event.audioAsset) {
          this.audioEngine.play("VOICE", event.audioAsset, { fadeInMs: 150, volume: 1 });
        }
        this.scheduleDuckRestore();
        break;

      case "ENVIRONMENT_AMBIENCE":
        if (event.audioAsset) {
          this.audioEngine.play("AMBIENCE", event.audioAsset, {
            loop: event.loop ?? true,
            volume: event.volume ?? 0.35
          });
        }
        break;

      case "ZOMBIE_ENCOUNTER":
        if (event.audioAsset) {
          this.audioEngine.play("SFX", event.audioAsset, {
            volume: 0.9,
            pan: event.pan ?? 0,
            fadeInMs: 200
          });
        }
        break;

      case "STORY_BEAT":
        // Text/artwork already applied above; a story beat may also carry a
        // one-shot cue (e.g. a gate creak) on whichever channel it specifies.
        if (event.audioAsset) {
          this.audioEngine.play(event.channel ?? "SFX", event.audioAsset, {
            volume: 0.8,
            pan: event.pan ?? 0,
            fadeInMs: 100
          });
        }
        break;

      case "MISSION_COMPLETE":
        if (event.audioAsset) {
          this.audioEngine.play("SFX", event.audioAsset, { volume: 0.9, fadeInMs: 100 });
        }
        this.complete();
        break;
    }
  }

  scheduleDuckRestore(afterMs = 4500) {
    clearTimeout(this._duckRestoreTimer);
    this._duckRestoreTimer = setTimeout(() => {
      this.audioEngine.restore("MUSIC", 0.55);
      this.audioEngine.restore("AMBIENCE", 0.35);
    }, afterMs);
  }

  complete() {
    this._running = false;
    clearTimeout(this._pendingTimeout);
    clearTimeout(this._duckRestoreTimer);
    this.setState({ isRunning: false, isComplete: true });
    // Stop everything except SFX so a mission-complete sting (if any) can finish playing.
    this.audioEngine.stop("MUSIC");
    this.audioEngine.stop("VOICE");
    this.audioEngine.stop("AMBIENCE");
  }

  stop() {
    this._running = false;
    clearTimeout(this._pendingTimeout);
    clearTimeout(this._duckRestoreTimer);
    this.audioEngine.stopAll();
    this.setState(this.initialState());
  }
}
