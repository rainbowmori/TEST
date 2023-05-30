package github.rainbowmori.test;

import com.google.gson.JsonObject;
import github.rainbowmori.rainbowapi.object.customblock.CustomBlock;
import github.rainbowmori.rainbowapi.object.customblock.CustomModelBlock;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LuckOreBlock extends CustomModelBlock {

  private final UUID uuid;

  public LuckOreBlock(Location location) {
    super(location);
    throw new RuntimeException("このブロックはプレイヤーの情報を必須とします");
  }

  public LuckOreBlock(Location location, JsonObject data) {
    super(location, data);
    uuid = UUID.fromString(data.get("uuid").getAsString());
    EffectBlock effectBlock = CustomBlock.blockCache.computeGetCache(uuid, new EffectBlock(uuid));
    if (effectBlock.hasLuckOre) {
      remove();
      return;
    }
    effectBlock.hasLuckOre = true;
  }

  public LuckOreBlock(Location location, Player player) {
    super(location, player);
    uuid = player.getUniqueId();
    EffectBlock effectBlock = CustomBlock.blockCache.computeGetCache(uuid, new EffectBlock(uuid));
    if (effectBlock.hasLuckOre) {
      dropItem(location);
      Util.send(player,"<red>LuckOreBlockはすでに設置しています(二つ以上は設置できません)");
      remove();
      //TODO ここremove()でclearData()もよばれてるからhasLuckOreがFalseになって設置できちゃうな
      return;
    }
    Util.send(player, "<yellow>これで鉱石を掘って確率で増えます");
    effectBlock.hasLuckOre = true;
  }

  @Override
  public JsonObject getBlockData() {
    JsonObject object = new JsonObject();
    object.addProperty("uuid",uuid.toString());
    return object;
  }

  @Override
  public void leftClick(PlayerInteractEvent e) {
    super.leftClick(e);
    Util.send(e.getPlayer(), "<red>これで鉱石を掘っても確率で増えなくなります");
  }

  @Override
  public void clearData(Location location) {
    super.clearData(location);
    CustomBlock.blockCache.computeGetCache(uuid, new EffectBlock(uuid)).hasLuckOre = false;
  }

  @Override
  public ItemStack getItem() {
    return new LuckOreItem().getItem();
  }

  @Override
  public String getIdentifier() {
    return getClass().getSimpleName();
  }
}
