package github.rainbowmori.test.npc;

import com.google.gson.JsonObject;
import github.rainbowmori.rainbowapi.RainbowAPI;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.TEST;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ChatNPC extends NPC{

  public ChatNPC(Location location, String id) {
    super(location, id);
  }

  public ChatNPC(Entity entity, String id) {
    super(entity, id);
  }

  @Override
  public void rightClick(Player player) {
    Util.send(player,"clicked ");
  }

  @Override
  public JsonObject toJson() {
    JsonObject jsonObject = super.toJson();
    return jsonObject;
  }

  @Override
  public String getNPCType() {
    return "ChatNPC";
  }

  public static void loadNPC(JsonObject json) {
    Location location = RainbowAPI.gson.fromJson(json.get("location"), Location.class);
    Component displayName = Util.mm(json.get("displayName").getAsString());
    String id = json.get("id").getAsString();
    EntityType entityType = EntityType.valueOf(json.get("entityType").getAsString().toUpperCase());
    UUID uuid = UUID.fromString(json.get("uuid").getAsString());
    boolean showName = json.get("showName").getAsBoolean();
    ChatNPC npc = location.getNearbyEntitiesByType(entityType.getEntityClass(), 1).stream()
        .filter(entity -> entity.getUniqueId().equals(uuid)).findFirst()
        .map(entity -> new ChatNPC(entity, id)).orElse(null);
    if (npc == null) {
      TEST.getRainbowAPI().prefixUtil.logError(id + "のChatNPCが読み込まれませんでした :" + json);
      return;
    }
    npc.setDisplayName(showName);
    npc.setDisplayName(displayName);
  }

  //ここでNPCLoadというclassでやっぱりrainbowapiでNPCAPIとして作ってloadNPCもregisterするとき
  // にTでNPCのclassを返してconsumerでさらにNPCをChatNPCに拡張してからすればいいと思う
}
