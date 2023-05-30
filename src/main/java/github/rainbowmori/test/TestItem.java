package github.rainbowmori.test;

import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.object.cutomitem.cooldown.CooldownItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestItem extends CustomItem implements CooldownItem {

  public TestItem() {
    this(new ItemBuilder(Material.STONE).name("TEST ITEM").build());
  }

  public TestItem(ItemStack item) {
    super(item);
  }

  @Override
  public void leftClick(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();
    if (hasCooldown(uuid)) {
      Util.cast("<red>is left cooldown!!!!");
      return;
    }
    addCooldown(uuid, 10);
    Util.cast("left");
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    UUID uuid = player.getUniqueId();
    if (hasCooldown(uuid)) {
      Util.cast("<red>is right cooldown!!!!");
      return;
    }
    addCooldown(uuid, 10);
    Util.cast("right");
  }

  @Override
  public @NotNull String getIdentifier() {
    return "test";
  }
}
