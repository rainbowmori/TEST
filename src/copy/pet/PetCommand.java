package github.rainbowmori.test.pet;

import github.rainbowmori.rainbowapi.object.commandapi.CommandPermission;
import github.rainbowmori.rainbowapi.object.commandapi.CommandTree;
import github.rainbowmori.rainbowapi.object.ui.button.ItemButton;
import github.rainbowmori.rainbowapi.object.ui.menu.MenuHolder;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.test.TEST;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PetCommand {

  private static final Map<Integer, EntityType> pets = new HashMap<>() {{
    put(0, EntityType.ZOMBIE);
    put(1, EntityType.CREEPER);
    put(2, EntityType.SKELETON);
    put(3, EntityType.WOLF);
    put(4, EntityType.CHICKEN);
    put(5, EntityType.BLAZE);
    put(6, EntityType.FOX);
    put(7, EntityType.SHEEP);
  }};

  private static final Map<EntityType, Integer> petSize = new HashMap<>() {{
    put(EntityType.ZOMBIE, 3);
    put(EntityType.CREEPER, 9);
    put(EntityType.SKELETON, 17);
    put(EntityType.WOLF, 45);
    put(EntityType.CHICKEN, 46);
    put(EntityType.BLAZE, 76);
    put(EntityType.FOX, 100);
    put(EntityType.SHEEP, 5000);
  }};

  private static final MenuHolder<TEST> petMenu;

  static {
    petMenu = new MenuHolder<>(TEST.getPlugin(), 9);
    pets.forEach((integer, entityType) -> petMenu.setButton(integer, new ItemButton<>(
        new ItemBuilder(Material.valueOf(entityType.name() + "_SPAWN_EGG")).name(
            petSize.getOrDefault(entityType, 0)).build()) {
      @Override
      public void onClick(MenuHolder<?> holder, InventoryClickEvent event) {
        HumanEntity clicked = event.getWhoClicked();
        Mob mob = (Mob) clicked.getWorld().spawnEntity(clicked.getLocation(),
            pets.getOrDefault(event.getRawSlot(), EntityType.ZOMBIE));
        mob.getEquipment().setHelmet(new ItemBuilder(Material.STONE).build());
        Bukkit.getMobGoals().removeAllGoals(mob);
        Integer size = petSize.getOrDefault(
            pets.getOrDefault(event.getRawSlot(), EntityType.ZOMBIE), 0);
        Bukkit.getMobGoals().addGoal(mob, 0, new PetMineGoal(mob, ((Player) clicked), size));
      }
    }));
  }

  public PetCommand() {
    new CommandTree("pets")
        .withPermission(CommandPermission.OP)
        .executesPlayer((sender, args) -> {
          sender.openInventory(petMenu.getInventory());
        }).register();
  }
}
