// Offline-first app shell cache. Bump CACHE_NAME whenever precached files change
// so clients pick up the new version instead of serving stale assets forever.
const CACHE_NAME = "runzombiez-v5";

const PRECACHE_URLS = [
  "./",
  "index.html",
  "manifest.webmanifest",
  "css/style.css",
  "js/app.js",
  "js/audio-engine.js",
  "js/mission-engine.js",
  "js/mission-repository.js",
  "js/demo-controller.js",
  "missions/demo_mission.json",
  "missions/outbreak_signal.json",
  "icons/icon.svg",
  "icons/icon-192.png",
  "icons/icon-512.png",
  "icons/icon-maskable-192.png",
  "icons/icon-maskable-512.png",
  "icons/apple-touch-icon.png",
  "art/branding/hero.webp",
  "art/story_panels/haven_gate.svg",
  "art/story_panels/gate_open.svg",
  "art/story_panels/gate_close.svg",
  "art/story_panels/zombie_alley.svg",
  "art/story_panels/zombie_chase.svg",
  "art/story_panels/overpass.svg",
  "art/story_panels/depot_interior.svg",
  "art/story_panels/transit_depot.svg"
];

self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(PRECACHE_URLS)).then(() => self.skipWaiting())
  );
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key)))
    ).then(() => self.clients.claim())
  );
});

// Cache-first for the app shell; anything not precached (e.g. audio, added
// later) falls back to the network and is cached opportunistically.
self.addEventListener("fetch", (event) => {
  if (event.request.method !== "GET") return;

  event.respondWith(
    caches.match(event.request).then((cached) => {
      if (cached) return cached;
      return fetch(event.request)
        .then((response) => {
          if (response.ok) {
            const clone = response.clone();
            caches.open(CACHE_NAME).then((cache) => cache.put(event.request, clone));
          }
          return response;
        })
        .catch(() => cached);
    })
  );
});
