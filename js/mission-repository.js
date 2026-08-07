// Loads bundled mission JSON. Offline-first: missions/ is cached by the
// service worker, so this works with no network after first load.
export class MissionRepository {
  constructor(basePath = "missions/") {
    this.basePath = basePath;
  }

  async loadMission(fileName) {
    const response = await fetch(this.basePath + fileName);
    if (!response.ok) throw new Error(`Could not load mission "${fileName}": ${response.status}`);
    return response.json();
  }
}
