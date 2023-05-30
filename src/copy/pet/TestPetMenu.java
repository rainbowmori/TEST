package github.rainbowmori.test.pet;

import github.rainbowmori.rainbowapi.object.ui.button.ClaimButton;
import github.rainbowmori.rainbowapi.object.ui.button.CurrentButton;
import github.rainbowmori.rainbowapi.object.ui.button.ItemButton;
import github.rainbowmori.rainbowapi.object.ui.menu.MenuHolder;
import github.rainbowmori.rainbowapi.object.ui.menu.PageMenu;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.TEST;
import java.util.Optional;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class TestPetMenu extends PageMenu<TEST> {

  private final PetMineGoal goal;

  public TestPetMenu(int pageSize, PetMineGoal goal) {
    this(pageSize, goal, 0, Math.min(goal.getMaxSlot(), pageSize));
  }

  @Override
  public void updateView() {
    getPage().update();
    super.updateView();
  }

  private TestPetMenu(int pageSize, PetMineGoal goal, int rewardStartIndex, int rewardEndIndex) {
    super(TEST.getPlugin(),
        new ItemPage(goal, pageSize, rewardStartIndex, rewardEndIndex), "Pet Mine Menu",
        null, null);
    this.goal = goal;
    getPage().enclosingMenu = this;
  }

  @Override
  public void onOpen(InventoryOpenEvent openEvent) {
    super.onOpen(openEvent);
    setButton(getPageSize() + 1,
        new CurrentButton<>(goal.getMob().getEquipment().getItemInMainHand(), itemStack ->
            goal.getMob().getEquipment().setItemInMainHand(itemStack)));
    setItem();
  }


  public void setItem() {
    int statsIndex = getPageSize() + 4;
    int otherIndex = getPageSize() + 3;
    if (goal.isMining()) {
      setButton(statsIndex, new ItemButton<>(
          new ItemBuilder(Material.BARRIER).name("クリックしてmobに採掘を停止するよう命令します。").build()) {
        @Override
        public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
          holder.getPlugin().getServer().getScheduler()
              .runTask(holder.getPlugin(), event.getView()::close);
          goal.stopMine();
          setItem();
        }
      });
      getInventory().setItem(otherIndex, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
    } else if (goal.isEditing()) {
      setButton(statsIndex, new ItemButton<>(
          new ItemBuilder(Material.STONE_PICKAXE).name("マイニングを開始。")
              .lore("クリックしてモブに命令し、選択したパスのマイニングを開始します。").build()) {
        @Override
        public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
          holder.getPlugin().getServer().getScheduler()
              .runTask(holder.getPlugin(), event.getView()::close);
          goal.startMine();
          setItem();
        }
      });
      setButton(otherIndex, new ItemButton<>(
          new ItemBuilder(Material.BARRIER).name("編集を停止")
              .lore("選択を解除し、編集モードを終了します。").build()) {
        @Override
        public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
          holder.getPlugin().getServer().getScheduler()
              .runTask(holder.getPlugin(), event.getView()::close);
          Util.send(event.getWhoClicked(),"編集を終了");
          goal.stopEdit();
          setItem();
        }
      });
    } else {
      setButton(statsIndex, new ItemButton<>(
          new ItemBuilder(Material.BARRIER).name("パスを設定")
              .lore("クリックして、この Mob のパスを選択します。", "ブロックを右クリックして選択します。",
                  "モブは、最初に選択したブロックの方向に採掘します。").build()) {
        @Override
        public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
          holder.getPlugin().getServer().getScheduler()
              .runTask(holder.getPlugin(), event.getView()::close);
          goal.startEdit();
          setItem();
        }
      });
      if (goal.getTargetList() != null && goal.getTargetList().length > 0) {
        setButton(otherIndex, new ItemButton<>(
            new ItemBuilder(Material.STONE_PICKAXE).name("マイニングを続ける")
                .lore("クリックしてmobに命令し、採掘を続けます。").build()) {
          @Override
          public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
            holder.getPlugin().getServer().getScheduler()
                .runTask(holder.getPlugin(), event.getView()::close);
            goal.continueMine();
            setItem();
          }
        });
      }else {
        getInventory().setItem(otherIndex, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
      }
    }
    for (int i = getPageSize(); i < (getPageSize() + 9); i++) {
      ItemStack item = getInventory().getItem(i);
      if (item == null || item.getType() == Material.AIR) {
        getInventory().setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
      }
    }
  }

  @Override
  public ItemPage getPage() {
    return (ItemPage) super.getPage();
  }

  @Override
  protected boolean needsRedirects() {
    return false;
  }

  @Override
  public Optional<Supplier<TestPetMenu>> getNextPageMenu() {
    ItemPage itemPage = getPage();
    if (itemPage.rewardEndIndex < goal.getMaxSlot()) {
      return Optional.of(() -> new TestPetMenu(
          goal.getInventorySize(itemPage.rewardEndIndex),
          goal,
          itemPage.rewardEndIndex,
          Math.min(goal.getMaxSlot(), itemPage.rewardEndIndex + 45)));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<Supplier<TestPetMenu>> getPreviousPageMenu() {
    ItemPage itemPage = getPage();
    if (itemPage.rewardStartIndex > 0) {
      return Optional.of(() -> new TestPetMenu(
          goal.getInventorySize(Math.max(itemPage.rewardStartIndex - 45, 0)),
          goal,
          Math.max(0, itemPage.rewardStartIndex - 45),
          itemPage.rewardStartIndex));
    } else {
      return Optional.empty();
    }
  }

  public static class ItemPage extends MenuHolder<TEST> {

    private final PetMineGoal goal;

    private final int rewardStartIndex, rewardEndIndex;
    private TestPetMenu enclosingMenu;

    private ItemPage(PetMineGoal goal, int pageSize, int rewardStartIndex,
        int rewardEndIndex) {
      super(TEST.getPlugin(), pageSize);
      this.goal = goal;
      this.rewardStartIndex = rewardStartIndex;
      this.rewardEndIndex = rewardEndIndex;
    }

    public void update() {
      for (int slot = 0; slot < getInventory().getSize()
          && Math.min(enclosingMenu.getPageSize(), goal.getItems().size() - rewardStartIndex) > slot; slot++) {
        setButton(slot, new ShiftingClaimButton(goal.getItems().get(rewardStartIndex + slot)));
      }
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
      for (int slot = 0; slot < getInventory().getSize()
          && Math.min(enclosingMenu.getPageSize(), goal.getItems().size() - rewardStartIndex) > slot; slot++) {
        setButton(slot, new ShiftingClaimButton(goal.getItems().get(rewardStartIndex + slot)));
      }
      if (rewardEndIndex == goal.getMaxSlot()) {
        for (int slot = rewardEndIndex - rewardStartIndex; slot < getInventory().getSize();
            slot++) {
          getInventory().setItem(slot, new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
        }
      }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
      clearButtons();
    }

    private void shiftButtons(int slotIndex) {
      int listIndex = rewardStartIndex + slotIndex;
      goal.getItems().remove(listIndex);

      while (slotIndex < getInventory().getSize()) {
        if (listIndex < goal.getItems().size()) {
          ItemStack reward = goal.getItems().get(listIndex);
          setButton(slotIndex, new ShiftingClaimButton(reward));
        } else {
          unsetButton(slotIndex);
        }

        slotIndex++;
        listIndex++;
      }

      enclosingMenu.getHostingPage().resetButtons();
    }
  }

  private static class ShiftingClaimButton extends ClaimButton<ItemPage> {

    public ShiftingClaimButton(ItemStack reward) {
      super(reward, (page, event, itemStack) -> page.shiftButtons(event.getSlot()));
    }
  }

}
