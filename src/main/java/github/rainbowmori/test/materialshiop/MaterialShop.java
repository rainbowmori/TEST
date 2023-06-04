package github.rainbowmori.test.materialshiop;

import github.rainbowmori.rainbowapi.object.anvilgui.AnvilGUI.ResponseAction;
import github.rainbowmori.rainbowapi.object.anvilgui.GUIs;
import github.rainbowmori.rainbowapi.object.ui.button.ItemButton;
import github.rainbowmori.rainbowapi.object.ui.button.RedirectItemButton;
import github.rainbowmori.rainbowapi.object.ui.menu.ItemInputMenu;
import github.rainbowmori.rainbowapi.object.ui.menu.MenuHolder;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.TEST;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MaterialShop {

  private static final Map<Material, MaterialInfo> AllMaterial = new HashMap<>();

  private static MenuHolder<TEST> shop = null;

  public static void openShop(Player player) {
    if (shop == null) {
      shop = new MenuHolder<>(TEST.getPlugin(), 54, "<red>SHOP") {{
        category.forEach((integer, shopCategory) -> setButton(integer, shopCategory.getButton()));
      }};
    }
    player.openInventory(shop.getInventory());
  }

  private static List<MaterialInfo> append(List<MaterialInfo> list) {
    list.forEach(materialInfo -> AllMaterial.put(materialInfo.material(), materialInfo));
    return list;
  }

  public static List<MaterialInfo> getMatchedValues(List<MaterialInfo> values, String str) {
    String[] s = str.split("_");
    return values.stream().filter(entry -> Arrays.stream(s)
            .anyMatch(part -> entry.material().name().toLowerCase().contains(part.toLowerCase())))
        .sorted((e1, e2) -> {
          String s1 = e1.material().name().toLowerCase();
          String s2 = e2.material().name().toLowerCase();
          int e1MatchCount = (int) Arrays.stream(s)
              .filter(part -> s1.contains(part.toLowerCase()))
              .count();
          int e2MatchCount = (int) Arrays.stream(s)
              .filter(part -> s2.contains(part.toLowerCase()))
              .count();
          return Integer.compare(e2MatchCount, e1MatchCount);
        }).toList();
  }



  private static final Map<Integer, ShopCategory> category = new HashMap<>() {{


    put(1, new ShopCategory(new ItemBuilder(Material.STONE).name("<gray>石系").build(),
        append(new ArrayList<>() {{
          add(new MaterialInfo(Material.STONE, 200, 100));
          add(new MaterialInfo(Material.ANDESITE, 103120, 200));
        }})));







    put(45, new ShopCategory(new RedirectItemButton<>(
        new ItemBuilder(Material.END_CRYSTAL).name("<aqua>クリックでアイテム検索").build(),
        () -> new ItemInputMenu<>(TEST.getPlugin(), "<aqua>ここに検索したいアイテムを入れてください",
            (itemStack, event) -> {
              Player whoClicked = (Player) event.getWhoClicked();
              if (itemStack == null) {
                whoClicked.openInventory(event.getInventory());
                return;
              }
              event.getWhoClicked().openInventory(
                  new ItemValueShopPage(TEST.getPlugin(),
                      Util.mm("<red>" + itemStack.getType().name()),
                      getMatchedValues(AllMaterial.values().stream().toList(),
                          itemStack.getType().name())).getInventory());
            }, (itemStack, event) -> openShop(((Player) event.getWhoClicked()))).getInventory())));
    put(46, new ShopCategory(new ItemButton<>(
        new ItemBuilder(Material.BOOK).name("<aqua>クリックで文字検索").build()) {
      @Override
      public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
        GUIs.of(TEST.getPlugin()).getStringInput("&b文字で検索", (s, completion) -> List.of(
                ResponseAction.openInventory(
                    new ItemValueShopPage(TEST.getPlugin(), Util.mm("<red>" + s),
                        MaterialShop.getMatchedValues(AllMaterial.values().stream().toList(),
                            s)).getInventory())))
            .setCloseable(MaterialShop::openShop)
            .open(((Player) event.getWhoClicked()));
      }
    }));
  }};

}
