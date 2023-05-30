package github.rainbowmori.test.pet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import github.rainbowmori.rainbowapi.RainbowAPI;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.TEST;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class PetMineGoal implements Goal<Mob>, Listener {

  private final GoalKey<Mob> key = GoalKey.of(Mob.class,
      new NamespacedKey(TEST.getPlugin(), "mob_miner"));

  private final Mob mob;
  private final Player owner;

  private final TestPetMenu petMenu;

  private final List<ItemStack> items;
  private final List<Location> selectedLocations = new ArrayList<>();
  private final List<MagmaCube> indicators = new ArrayList<>();
  private final int MaxSlot;
  private boolean mining = false;
  private boolean editMode = false;
  private Location targetBlock;
  private Location[] targetList;
  private int failedAttempts;
  private float progress;
  private long interactCoolDown;

  public PetMineGoal(Mob mob, Player owner, int size) {
    this.mob = mob;
    mob.getEquipment().setItemInMainHand(getDefaultMainHand());
    this.owner = owner;
    this.items = new ArrayList<>();
    this.MaxSlot = size;
    this.petMenu = new TestPetMenu(getInventorySize(0), this);
  }

  public int getMaxSlot() {
    return MaxSlot;
  }

  public Location[] getTargetList() {
    return targetList;
  }

  @Override
  public boolean shouldActivate() {
    return true;
  }

  @Override
  public boolean shouldStayActive() {
    return shouldActivate();
  }

  @Override
  public void start() {
    RainbowAPI.manager.registerEvents(this, TEST.getPlugin());
  }

  public ItemStack getDefaultMainHand() {
    return new ItemBuilder(Material.STONE_PICKAXE).name("Main Hand")
        .lore("<aqua>ここにアイテムをドラッグしてモブに装備させます。").build();
  }

  @Override
  public void stop() {
    HandlerList.unregisterAll(this);
  }

  @Override
  public void tick() {
    mob.setTarget(null);
    if (!mob.getWorld().equals(owner.getWorld())) {
      mob.remove();
      return;
    }
    if (targetList == null || !mining) {
      targetBlock = null;
      progress = 0f;
      failedAttempts = 0;
      return;
    }

    if (targetBlock == null) {
      for (Location l : targetList) {
        if (l.getBlock().getType().equals(Material.AIR)) {
          continue;
        }
        targetBlock = l;
        break;
      }
      if (targetBlock == null) {
        failedAttempts++;
        if (failedAttempts >= 16) {
          stopMine();
          updateOwnerMenu();
          targetList = null;
          return;
        }
      }
      return;
    }

    if (targetBlock.getBlock().getType() == Material.AIR) {
      targetBlock = null;
      return;
    }

    mob.lookAt(targetBlock);
    mob.getPathfinder().moveTo(shiftLocation(targetBlock, targetList[0], -1));
    if (mob.getLocation().distance(targetBlock) > 4) {
      return;
    }

    failedAttempts = 0;
    float mineSpeed = 10; //targetBlock.getBlock().getDestroySpeed(mob.getEquipment().getItemInMainHand(),true);
    progress += mineSpeed;
    if (progress >= 100) {
      progress = 0f;
      sendBlockChange(targetBlock, 0f, 0);
      if (!breakBlock(targetBlock)) {
        Util.send(owner, "<red>ブロック破壊でerrorが起きました(正しいツールを設定してください)");
        stopMine();
        updateOwnerMenu();
      }
      targetBlock = null;
      return;
    }

    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    PacketContainer packet = protocolManager.createPacket(Server.ANIMATION);
    packet.getIntegers().write(0, mob.getEntityId());
    packet.getIntegers().write(1, 0);
    for (Player player : mob.getWorld()
        .getNearbyEntitiesByType(Player.class, mob.getLocation(), 16D)) {
      try {
        protocolManager.sendServerPacket(player, packet);
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    sendBlockChange(targetBlock, progress, 0);
  }

  @Override
  public @NotNull GoalKey<Mob> getKey() {
    return key;
  }

  @Override
  public @NotNull EnumSet<GoalType> getTypes() {
    return EnumSet.of(GoalType.TARGET);
  }

  @EventHandler
  public void entityRemove(EntityRemoveFromWorldEvent event) {
    if (event.getEntity().equals(mob)) {
      clearAll();
    }
  }

  @EventHandler
  public void interact(PlayerInteractEvent event) {
    if (!event.getPlayer().equals(owner) || !editMode) {
      return;
    }
    if (interactCoolDown > System.currentTimeMillis()) {
      return;
    }
    interactCoolDown = System.currentTimeMillis() + (50L * 2);

    if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
      Location add = event.getClickedBlock().getLocation().add(.5, .5, .5);
      selectLocation(add, event.getBlockFace());
    }
  }

  @EventHandler
  public void interactEntity(PlayerInteractEntityEvent event) {
    if (!event.getPlayer().equals(owner) || !event.getRightClicked().equals(mob)) {
      return;
    }
    event.getPlayer().openInventory(petMenu.getInventory());
  }

  public void updateOwnerMenu() {
    petMenu.updateView();
  }

  public int getInventorySize(int startIndex) {
    int quotient = (int) Math.ceil((getMaxSlot() - startIndex) / 9f);
    return quotient > 5 ? 45 : quotient * 9;
  }

  private boolean breakBlock(Location targetBlock) {
    Block block = targetBlock.getBlock();
    Collection<ItemStack> drops = block.getDrops(mob.getEquipment().getItemInMainHand());
    List<ItemStack> itemStacks = new ArrayList<>(items);
    for (ItemStack drop : drops) {
      if (!canGiveItem(itemStacks, drop)) {
        return false;
      }
    }
    for (ItemStack drop : drops) {
      canGiveItem(items, drop);
    }
    block.setType(Material.AIR);
    updateOwnerMenu();
    return true;
  }

  private boolean canGiveItem(List<ItemStack> items, ItemStack itemStack) {
    ItemStack item = itemStack.clone();
    if (items.isEmpty()) {
      items.add(item);
      return true;
    }
    int index = -1;
    for (ItemStack i : items) {
      index++;
      if (i.isSimilar(item)) {
        if (i.getAmount() == i.getMaxStackSize()) {
          continue;
        }
        if (i.getAmount() + item.getAmount() > i.getMaxStackSize()) {
          items.set(index, new ItemBuilder(i).amount(i.getMaxStackSize()).build());
          if (items.size() + 1 > getMaxSlot()) {
            return false;
          }
          items.add(index + 1,
              new ItemBuilder(item).amount(i.getAmount() + item.getAmount() - i.getMaxStackSize())
                  .build());
        } else {
          items.set(index, new ItemBuilder(i).amount(i.getAmount() + item.getAmount()).build());
        }
        return true;
      }
    }
    if (items.size() + 1 > getMaxSlot()) {
      return false;
    }
    items.add(item);
    return true;
  }

  private void sendBlockChange(Location loc, float progress, int id) {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    PacketContainer packet = protocolManager.createPacket(
        PacketType.Play.Server.BLOCK_BREAK_ANIMATION);

    packet.getIntegers().write(0, id).write(1, Math.round(progress * .09f));
    packet.getBlockPositionModifier()
        .write(0, new BlockPosition(new Vector(loc.getX(), loc.getY(), loc.getZ())));

    for (Player p : loc.getWorld().getNearbyEntitiesByType(Player.class, loc, 16D)) {
      try {
        protocolManager.sendServerPacket(p, packet);
      } catch (InvocationTargetException e) {
        throw new RuntimeException("Cannot send packet " + packet, e);
      }
    }
  }

  private Location shiftLocation(Location loc, Location directionLoc, double forward) {
    return loc.clone().add(directionLoc.clone().getDirection().multiply(forward));
  }

  public boolean isMining() {
    return mining;
  }

  public List<ItemStack> getItems() {
    return items;
  }

  public Mob getMob() {
    return mob;
  }

  private void clearAll() {
    HandlerList.unregisterAll(this);
    clearSelection();
  }

  public void continueMine() {
    Util.send(owner, "採掘を引き続き開始");
    if (targetList == null || targetList.length < 1) {
      mining = false;
      return;
    }
    mining = true;
  }

  public void startMine() {
    Util.send(owner, "採掘を開始");
    if (selectedLocations.isEmpty()) {
      mining = false;
      stopEdit();
      return;
    }

    targetList = selectedLocations.toArray(Location[]::new);
    mining = true;
    stopEdit();
  }

  public void stopMine() {
    Util.send(owner, "採掘を終了");
    mining = false;
  }

  public void startEdit() {
    editMode = true;
    Util.title(owner, "右クリックで", "ブロックを追加します。", 3);
  }

  public void stopEdit() {
    clearSelection();
    editMode = false;
  }

  public boolean isEditing() {
    return editMode;
  }

  private void selectLocation(Location location, BlockFace face) {
    boolean foundAdj = false;
    for (Location l : selectedLocations) {
      if (matchLoc(l, location)) {
        clearSelection();
        Util.send(owner, "選択リセット。");
        owner.playSound(owner, Sound.BLOCK_NOTE_BLOCK_HAT, 1f, .6f);
        return;
      }

      if (!foundAdj) {
        for (Location adjL : getAdjLocation(l)) {
          if (matchLoc(adjL, location)) {
            foundAdj = true;
          }
        }
      }
    }
    if (selectedLocations.size() >= 40) {
      Util.send(owner, "最大選択サイズ。");
      owner.playSound(owner.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, .6f);
      return;
    }

    if (!selectedLocations.isEmpty() && !foundAdj) {
      Util.send(owner, "半径1ブロック以内のブロックにしてください");
      owner.playSound(owner.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, .6f);
      return;
    }

    if (selectedLocations.isEmpty()) {
      location.setDirection(face.getDirection().multiply(-1));
    }
    selectedLocations.add(location);
    indicators.add(
        location.getWorld().spawn(location.clone().subtract(0, 0.25, 0), MagmaCube.class, e -> {
          e.setInvulnerable(true);
          e.setAI(false);
          e.setGravity(false);
          e.setInvisible(true);
          e.setGlowing(true);
          e.setSize(1);
          e.setCollidable(false);
        }));
    owner.playSound(owner.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1.2f);
  }

  private boolean matchLoc(Location loc1, Location loc2) {
    return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY()
        && loc1.getBlockZ() == loc2.getBlockZ();
  }

  private void clearSelection() {
    selectedLocations.clear();
    indicators.forEach(Entity::remove);
    indicators.clear();
  }

  private List<Location> getAdjLocation(Location center) {
    ArrayList<Location> locations = new ArrayList<>();
    for (int x = -1; x < 2; x++) {
      for (int y = -1; y < 2; y++) {
        for (int z = -1; z < 2; z++) {
          locations.add(center.clone().add(x, y, z));
        }
      }
    }
    return locations;
  }

}
