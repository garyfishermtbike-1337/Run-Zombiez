import { AudioEngine } from "./audio-engine.js";
import { MissionEngine } from "./mission-engine.js";
import { MissionRepository } from "./mission-repository.js";
import { DemoController } from "./demo-controller.js";

const FIRST_MISSION_FILE = "outbreak_signal.json";

const audioEngine = new AudioEngine("audio/");
const missionRepository = new MissionRepository("missions/");
const missionEngine = new MissionEngine(audioEngine);
const demoController = new DemoController(missionRepository, missionEngine);

const screens = {
  home: document.getElementById("screen-home"),
  mission: document.getElementById("screen-mission"),
  help: document.getElementById("screen-help")
};

function showScreen(name) {
  Object.entries(screens).forEach(([key, el]) => el.classList.toggle("active", key === name));
}

// ---- Story panel artwork cache: fetch inline SVG once, reuse from then on ----
const artworkCache = new Map();
async function renderArtwork(path) {
  const container = document.getElementById("mission-artwork");
  if (!path) {
    container.innerHTML = "";
    return;
  }
  if (!artworkCache.has(path)) {
    try {
      const svgText = await fetch(path).then((r) => (r.ok ? r.text() : ""));
      artworkCache.set(path, svgText);
    } catch {
      artworkCache.set(path, "");
    }
  }
  container.innerHTML = artworkCache.get(path) || "";
}

// ---- Mission screen state rendering ----
let lastArtwork = null;
missionEngine.onChange((state) => {
  document.getElementById("mission-name").textContent = state.mission?.name ?? "";
  document.getElementById("mission-speaker").textContent = state.currentSpeaker?.toUpperCase() ?? "";
  document.getElementById("mission-text").textContent = state.currentText ?? "";

  const label = document.getElementById("mission-label");
  label.textContent = state.isComplete
    ? "MISSION COMPLETE — WELCOME HOME, RUNNER 007"
    : "MISSION IN PROGRESS";
  label.classList.toggle("complete", state.isComplete);

  document.getElementById("btn-mission-stop").hidden = state.isComplete;
  document.getElementById("btn-mission-home").hidden = !state.isComplete;

  if (state.currentArtwork !== lastArtwork) {
    lastArtwork = state.currentArtwork;
    renderArtwork(state.currentArtwork);
  }
});

// ---- Button wiring ----
// audioEngine.resume() is fire-and-forget: if the browser's autoplay policy
// keeps the AudioContext suspended (e.g. no genuine user gesture recognized),
// its promise can stay pending indefinitely. Screen navigation and the
// mission timeline must never block on it.
document.getElementById("btn-start").addEventListener("click", async () => {
  audioEngine.resume();
  showScreen("mission");
  const mission = await missionRepository.loadMission(FIRST_MISSION_FILE);
  missionEngine.start(mission);
});

document.getElementById("btn-demo").addEventListener("click", () => {
  audioEngine.resume();
  showScreen("mission");
  demoController.run();
});

document.getElementById("btn-stop").addEventListener("click", () => {
  missionEngine.stop();
});

document.getElementById("btn-help").addEventListener("click", () => showScreen("help"));
document.getElementById("btn-help-back").addEventListener("click", () => showScreen("home"));

document.getElementById("btn-mission-stop").addEventListener("click", () => {
  missionEngine.stop();
  showScreen("home");
});

document.getElementById("btn-mission-home").addEventListener("click", () => {
  showScreen("home");
});

// ---- Offline-first: register service worker ----
if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker.register("service-worker.js").catch((err) => {
      console.warn("Service worker registration failed:", err);
    });
  });
}
