package github.rainbowmori.test;

import github.rainbowmori.rainbowapi.object.cutomitem.CoolityItem;
import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.object.cutomitem.cooldown.CooldownItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OfroAlertItem extends CustomItem implements CoolityItem {

  public OfroAlertItem() {
    this(new ItemBuilder(Material.NETHER_STAR).name("<red>OfroAlert").build());
  }

  public OfroAlertItem(ItemStack item) {
    super(item);
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();
    if (CooldownItem.hasCooldown(uuid, "OfroSonerItem")) {
      TEST.getPlugin().getPrefixUtil().send(player, "<red>OfroSonerと同時使用はできません");
      return;
    }
    if (hasCooldown(uuid)) {
      TEST.getPlugin().getPrefixUtil().send(player, "<red>すでにOfroAlertを使用しています");
      return;
    }
    if (useDurability(1)) {
      itemUse();
      return;
    }
    addCooldown(uuid, 300);
    Util.send(player, "<green>OfroAlertの機能が開始しました");
  }

  @Override
  public @NotNull Optional<String> getActionBarMessage(UUID uuid) {
    Player player = Objects.requireNonNull(Bukkit.getPlayer(uuid));
    Location location = player.getLocation();
    Optional<Player> min = player.getWorld().getNearbyEntitiesByType(Player.class, location, 30)
        .stream().filter(
            p -> !p.equals(player) && !CooldownItem.hasCooldown(p.getUniqueId(), "HideAmpouleItem"))
        .min(Comparator.comparingDouble(o -> location.distance(o.getLocation())));
    min.ifPresent(p -> Util.send(player, "<red>OfroAlertの探知にひっかりました!"));
    return Optional.of(
        (min.isPresent() ? "<red>OfroAlertの探知にひっかりました! " : " ") + getCooldownMessage(uuid)
            + " " + getDurabilityMessage(uuid));
  }

  @Override
  public @NotNull String getIdentifier() {
    return "OfroAlertItem";
  }

  @Override
  public int getMaxDurability() {
    return 3;
  }
}
