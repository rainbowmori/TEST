package github.rainbowmori.test.materialshiop;

import github.rainbowmori.rainbowapi.object.ui.button.MenuButton;
import github.rainbowmori.rainbowapi.object.ui.button.RedirectItemButton;
import github.rainbowmori.test.TEST;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class ShopCategory {

  private final MenuButton<?> button;

  public ShopCategory(ItemStack itemStack, List<MaterialInfo> materials) {
    this.button = new RedirectItemButton<>(itemStack, () -> new ItemValueShopPage(TEST.getPlugin(), itemStack.displayName(), materials).getInventory());
  }

  public ShopCategory(MenuButton<?> button) {
    this.button = button;
  }

  public MenuButton<?> getButton() {
    return button;
  }
}
