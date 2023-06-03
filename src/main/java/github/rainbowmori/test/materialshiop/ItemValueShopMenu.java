package github.rainbowmori.test.materialshiop;

import github.rainbowmori.rainbowapi.object.ui.menu.MenuHolder;
import github.rainbowmori.test.TEST;
import java.util.List;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public final class ItemValueShopMenu extends MenuHolder<TEST> {

  final int rewardStartIndex, rewardEndIndex;
  final List<MaterialInfo> values;
  public ItemValueShopPage page;

  public ItemValueShopMenu(TEST plugin, List<MaterialInfo> values, int rewardStartIndex,
      int rewardEndIndex) {
    super(plugin, 45, "<red>SHOP");
    this.rewardStartIndex = rewardStartIndex;
    this.rewardEndIndex = rewardEndIndex;
    this.values = values;
  }

  @Override
  public void onOpen(InventoryOpenEvent event) {
    for (int slot = 0; slot < getInventory().getSize() && rewardStartIndex + slot < rewardEndIndex; slot++) {
      MaterialInfo value = values.get(rewardStartIndex + slot);
      if (value == null) {
        continue;
      }
      setButton(slot, value.createButton(page.getInventory()));
    }
  }

  @Override
  public void onClose(InventoryCloseEvent event) {
    clearButtons();
  }
}
