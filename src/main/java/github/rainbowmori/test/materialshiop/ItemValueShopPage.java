package github.rainbowmori.test.materialshiop;

import github.rainbowmori.rainbowapi.object.anvilgui.AnvilGUI.ResponseAction;
import github.rainbowmori.rainbowapi.object.anvilgui.GUIs;
import github.rainbowmori.rainbowapi.object.ui.button.ItemButton;
import github.rainbowmori.rainbowapi.object.ui.button.RedirectItemButton;
import github.rainbowmori.rainbowapi.object.ui.menu.ItemInputMenu;
import github.rainbowmori.rainbowapi.object.ui.menu.MenuHolder;
import github.rainbowmori.rainbowapi.object.ui.menu.PageMenu;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.test.TEST;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public final class ItemValueShopPage extends PageMenu<TEST> {

  private final List<MaterialInfo> values;
  private final Component title;

  public ItemValueShopPage(TEST plugin, Component title, List<MaterialInfo> values) {
    this(plugin, title, values, 0, Math.min(values.size(), 45));
  }

  private ItemValueShopPage(TEST plugin, Component title, List<MaterialInfo> values,
      int rewardStartIndex,
      int rewardEndIndex) {
    super(plugin, new ItemValueShopMenu(plugin, values, rewardStartIndex, rewardEndIndex),
        "<red>SHOP", null, null);
    getPage().page = this;
    this.values = values;
    this.title = title;
  }

  @Override
  public void onOpen(InventoryOpenEvent openEvent) {
    super.onOpen(openEvent);
    setButton(getPageSize(), new ItemButton<>(
        new ItemBuilder(Material.ENDER_PEARL).name("<green>カテゴリー選択に戻る").build()) {
      @Override
      public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
        MaterialShop.openShop(((Player) event.getWhoClicked()));
      }
    });
    setButton(getPageSize() + 7, new RedirectItemButton<>(
        new ItemBuilder(Material.END_CRYSTAL).name("<aqua>クリックでアイテム検索").build(),
        () -> new ItemInputMenu<>(getPlugin(), "<aqua>ここに検索したいアイテムを入れてください",
            (itemStack, event) -> {
              Player whoClicked = (Player) event.getWhoClicked();
              if (itemStack == null) {
                whoClicked.openInventory(getInventory());
                return;
              }
              event.getWhoClicked().openInventory(
                  new ItemValueShopPage(getPlugin(), title,
                      MaterialShop.getMatchedValues(values,
                          itemStack.getType().name())).getInventory());
            }, (itemStack, event) -> event.getWhoClicked().openInventory(
            new ItemValueShopPage(getPlugin(), title, values).getInventory())).getInventory()));
    setButton(getPageSize() + 8,
        new ItemButton<>(new ItemBuilder(Material.BOOK).name("<aqua>クリックで文字検索").build()) {
          @Override
          public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
            GUIs.of(TEST.getPlugin()).getStringInput("&b文字で検索", (s, completion) -> List.of(
                    ResponseAction.openInventory(new ItemValueShopPage(getPlugin(), title,
                        MaterialShop.getMatchedValues(values, s)).getInventory())))
                .setCloseable(player -> player.openInventory(getInventory()))
                .open(((Player) event.getWhoClicked()));
          }
        });
  }

  @Override
  protected boolean needsRedirects() {
    return false;
  }

  @Override
  public ItemValueShopMenu getPage() {
    return (ItemValueShopMenu) super.getPage();
  }

  @Override
  public Optional<Supplier<ItemValueShopPage>> getNextPageMenu() {
    ItemValueShopMenu itemPage = getPage();
    int maxKey = itemPage.values.size();
    if (itemPage.rewardEndIndex < maxKey) {
      return Optional.of(() -> new ItemValueShopPage(
          getPlugin(),
          title,
          itemPage.values,
          itemPage.rewardEndIndex,
          Math.min(maxKey, itemPage.rewardEndIndex + getPageSize())));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Supplier<ItemValueShopPage>> getPreviousPageMenu() {
    ItemValueShopMenu itemPage = getPage();
    if (itemPage.rewardStartIndex > 0) {
      return Optional.of(() -> new ItemValueShopPage(
          getPlugin(),
          title,
          itemPage.values,
          Math.max(0, itemPage.rewardStartIndex - getPageSize()),
          Math.min(itemPage.rewardStartIndex, itemPage.values.size())));
    } else {
      return Optional.empty();
    }
  }
}