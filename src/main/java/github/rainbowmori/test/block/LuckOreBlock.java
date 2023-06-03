package github.rainbowmori.test.block;

import com.google.gson.JsonObject;
import github.rainbowmori.rainbowapi.object.customblock.CustomBlock;
import github.rainbowmori.rainbowapi.object.customblock.CustomModelBlock;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.item.LuckOreItem;
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
    EffectBlocks effectBlocks = CustomBlock.blockCache.computeGetCache(uuid, new EffectBlocks(uuid));
    if (effectBlocks.hasLuckOre) {
      super.clearData(getLocation());
      silentRemove();
      return;
    }
    effectBlocks.hasLuckOre = true;
  }

  public LuckOreBlock(Location location, Player player) {
    super(location, player);
    uuid = player.getUniqueId();
    EffectBlocks effectBlocks = CustomBlock.blockCache.computeGetCache(uuid, new EffectBlocks(uuid));
    if (effectBlocks.hasLuckOre) {
      dropItem(location);
      Util.send(player,"<red>LuckOreBlockはすでに設置しています(二つ以上は設置できません)");
      super.clearData(getLocation());
      silentRemove();
      return;
    }
    Util.send(player, "<yellow>これで鉱石を掘って確率で増えます");
    effectBlocks.hasLuckOre = true;
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
    CustomBlock.blockCache.computeGetCache(uuid, new EffectBlocks(uuid)).hasLuckOre = false;
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
