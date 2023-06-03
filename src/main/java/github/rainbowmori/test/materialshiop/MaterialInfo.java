package github.rainbowmori.test.materialshiop;

import github.rainbowmori.rainbowapi.object.anvilgui.AnvilGUI.ResponseAction;
import github.rainbowmori.rainbowapi.object.anvilgui.GUIs;
import github.rainbowmori.rainbowapi.object.ui.button.ItemButton;
import github.rainbowmori.rainbowapi.object.ui.button.MenuButton;
import github.rainbowmori.rainbowapi.object.ui.menu.YesNoMenu;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.test.ofro.ItemUtil;
import github.rainbowmori.test.ofro.OfroPrefix;
import github.rainbowmori.test.TEST;
import java.util.List;
import java.util.Optional;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @param buyPrice  購入価格（システムからプレイヤーへの価格）
 * @param sellPrice 販売価格（プレイヤーからシステムへ売る価格）
 */
public record MaterialInfo(Material material, int buyPrice, int sellPrice) {

  @Override
  public String toString() {
    return "ItemValue{" +
        "material=" + material.name() +
        ", buyPrice=" + buyPrice +
        ", sellPrice=" + sellPrice +
        '}';
  }

  public void selling(Player player, int amount) {
    ItemStack itemStack = new ItemStack(material);
    if (!ItemUtil.hasItemStack(player, itemStack, amount)) {
      OfroPrefix.SHOP.send(player,
          "<red>必要数アイテムを所持していません <gray>(%s/%s)".formatted(
              ItemUtil.hasItemStack(player, itemStack),
              amount));
      return;
    }
    Economy economy = TEST.getEconomy();
    int money = sellPrice() * amount;
    economy.depositPlayer(player, money);
    ItemUtil.removeItemStack(player, itemStack, amount);
    OfroPrefix.SHOP.send(player, "<green>%s個売却して%s円獲得しました".formatted(amount, money));
  }

  public void buying(Player player, int amount) {
    ItemStack itemStack = new ItemStack(material);
    Economy economy = TEST.getEconomy();
    int money = buyPrice() * amount;
    if (!economy.has(player, money)) {
      OfroPrefix.SHOP.send(player,
          "<red>所持金が足りないです!<gray>(%s/%s)".formatted(economy.getBalance(player), money));
      return;
    }
    player.getInventory().addItem(new ItemBuilder(itemStack).amount(amount).build());
    economy.withdrawPlayer(player, money);
    OfroPrefix.SHOP.send(player, "<green>%s円でアイテムを%s個購入しました!".formatted(money, amount));
  }

  public MenuButton<ItemValueShopMenu> createButton(@NotNull Inventory back) {
    ItemStack display = new ItemBuilder(material).addLore("").addLore(
            canSell() ? "<yellow>売値:" + sellPrice() + "円 <gray>(左クリック)" : "<red>売却はできません")
        .addLore(
            canBuy() ? "<green>買値:" + buyPrice() + "円 <gray>(右クリック)" : "<red>購入はできません")
        .build();
    return new ItemButton<>(display) {
      @Override
      public void onClick(ItemValueShopMenu holder, InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        TEST plugin = holder.getPlugin();
        if (event.getClick().isLeftClick()) {
          if (!canSell()) {
            OfroPrefix.SHOP.send(player, "<red>このアイテムを売却はできません");
            return;
          }
          GUIs.of(plugin).getIntegerInput("<yellow>売却する個数を入力してください",
                  integer -> integer > 0, (integer, completion) -> List.of(
                      ResponseAction.openInventory(new YesNoMenu<>(
                          plugin,
                          "<green>%s個を売却します 合計:%s円".formatted(integer,
                              sellPrice() * integer), yes -> {
                        Player p = (Player) yes.getWhoClicked();
                        selling(p, integer);
                        p.openInventory(back);
                      }, no -> {
                        Player p = (Player) no.getWhoClicked();
                        OfroPrefix.SHOP.send(p, "<red>売却をキャンセルしました");
                        p.openInventory(back);
                      }) {
                        @Override
                        public Optional<ItemStack> getMiddleItem() {
                          return Optional.of(new ItemStack(material));
                        }
                      }.getInventory())),
                  (integer, completion) -> List.of(ResponseAction.replaceInputText("1以上を入力してください")))
              .setCloseable(p -> {
                OfroPrefix.SHOP.send(p, "<red>売却をキャンセルしました");
                p.openInventory(back);
              })
              .open(player);
        } else {
          if (!canBuy()) {
            OfroPrefix.SHOP.send(player, "<red>このアイテムを購入はできません");
            return;
          }
          GUIs.of(plugin).getIntegerInput("<green>購入する個数を入力してください",
                  integer -> integer > 0, (integer, completion) -> List.of(
                      ResponseAction.openInventory(new YesNoMenu<>(plugin,
                          "<green>%s個を購入します 合計:%s円".formatted(integer,
                              buyPrice() * integer), yes -> {
                        Player p = (Player) yes.getWhoClicked();
                        buying(p, integer);
                        p.openInventory(back);
                      }, no -> {
                        Player p = (Player) no.getWhoClicked();
                        OfroPrefix.SHOP.send(p, "<red>購入をキャンセルしました");
                        p.openInventory(back);
                      }) {
                        @Override
                        public Optional<ItemStack> getMiddleItem() {
                          return Optional.of(new ItemStack(material));
                        }
                      }.getInventory())),
                  (integer, completion) -> List.of(ResponseAction.replaceInputText("1以上を入力してください")))
              .setCloseable(p -> {
                OfroPrefix.SHOP.send(p, "<red>購入をキャンセルしました");
                p.openInventory(back);
              })
              .open(player);
        }
      }
    };
  }

  /**
   * アイテムを販売できるかを説明します
   *
   * @return プレイヤーがシステムに販売することができるか
   */
  public boolean canSell() {
    return sellPrice() > 0;
  }


  /**
   * アイテムが購入できるかを説明します
   *
   * @return プレイヤーがシステムから購入することができるか
   */
  public boolean canBuy() {
    return buyPrice() > 0;
  }


  /**
   * アイテムの売却価格を取得します。
   *
   * @return 販売価格（システムからプレイヤーへの価格）
   */
  @Override
  public int sellPrice() {
    return sellPrice;
  }

  /**
   * アイテムの購入価格を取得します。
   *
   * @return 購入価格（プレイヤーからシステムへの価格）
   */
  @Override
  public int buyPrice() {
    return buyPrice;
  }
}
