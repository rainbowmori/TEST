package github.rainbowmori.test.item;

import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.object.cutomitem.cooldown.CooldownItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.TEST;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HideAmpouleItem extends CustomItem implements CooldownItem {

  public HideAmpouleItem() {
    super(new ItemBuilder(Material.POTION).name("<gray>HideAmpoule").build());
  }

  public HideAmpouleItem(ItemStack item) {
    super(item);
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();
    if (hasCooldown(uuid)) {
      TEST.getPlugin().getPrefixUtil().send(player,"<red>すでにHideAmpouleを使用しています");
      return;
    }
    Util.send(player,"<green>HideAmpouleItemを使用しました!300秒の間OfroAlertやOfroSonerから隠れられます");
    itemUse();
    addCooldown(uuid, 300);
  }

  @Override
  public @NotNull String getIdentifier() {
    return "HideAmpouleItem";
  }
}
