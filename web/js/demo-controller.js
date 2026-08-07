const DEMO_MISSION_FILE = "demo_mission.json";

// One-tap showcase: plays the bundled demo mission through the real
// MissionEngine, so the demo is an honest preview, not a separate code path.
export class DemoController {
  constructor(missionRepository, missionEngine) {
    this.missionRepository = missionRepository;
    this.missionEngine = missionEngine;
  }

  async run() {
    const mission = await this.missionRepository.loadMission(DEMO_MISSION_FILE);
    await this.missionEngine.start(mission);
  }
}
