package github.rainbowmori.test.item;

import github.rainbowmori.rainbowapi.object.cutomitem.CustomItem;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.test.block.LuckOreBlock;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LuckOreItem extends CustomItem {

  public LuckOreItem() {
    this(new ItemBuilder(Material.STONE_HOE).customModelData(1).name("<white>LuckOreItem").build());
  }

  public LuckOreItem(ItemStack item) {
    super(item);
  }

  @Override
  public void rightClick(PlayerInteractEvent e) {
    if (!clickedBlock(e)) {
      return;
    }
    relativeLocation(e, location -> new LuckOreBlock(location,e.getPlayer()));
    itemUse();
  }

  @Override
  public @NotNull String getIdentifier() {
    return getClass().getSimpleName();
  }

  @Override
  public @NotNull Optional<String> getActionBarMessage(Player player) {
    return Optional.empty();
  }
}
