package github.rainbowmori.test.materialshiop;

import github.rainbowmori.rainbowapi.object.ui.button.MenuButton;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ShopCategory {
  STONE(1,new ItemBuilder(Material.STONE).name("<gray>石系"),new ArrayList<MaterialInfo>(){{
    add();
  }}),

  MATERIAL_SEARCH(50,);

  private final int slot;
  private final ItemStack itemStack;
  private final List<MaterialInfo> materials;
  private final MenuButton<?> button;

  ShopCategory(int slot, ItemStack itemStack, List<MaterialInfo> materials) {
    this.slot = slot;
    this.itemStack = itemStack;
    this.materials = materials;
  }

  public int getSlot() {
    return slot;
  }

  public MenuButton<?> getButton() {
    return button;
  }
}
