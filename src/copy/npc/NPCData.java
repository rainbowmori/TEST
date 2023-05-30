package github.rainbowmori.test.npc;

import github.rainbowmori.rainbowapi.api.JsonAPI;
import github.rainbowmori.test.TEST;

public class NPCData extends JsonAPI {

  public NPCData() {
    super(TEST.getRainbowAPI(), "NPC.json");
  }
}
