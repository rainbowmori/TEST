package github.rainbowmori.test;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class Duck {

  private final HashMap<String, ArmorStand> parts = new HashMap<>();

  public Duck(double x, double y, double z) {
    Location location = new Location(Bukkit.getWorld("world"), x, y, z);

    ArmorStand body = getNewArmorStand(location, false, true);

    ArmorStand head = getNewArmorStand(location.clone().add(0, 0.2, 0.2), false, true);

    ArmorStand leftFeather = getNewArmorStand(location.clone().add(0.43, 0.2, 0.5), false, true);
    ArmorStand rightFeather = getNewArmorStand(location.clone().add(-0.04, 0.2, 0.5), false, true);

    ArmorStand leftFeet = getNewArmorStand(location.clone().add(0.4, 0.3, 0.2), false, true);
    ArmorStand rightFeet = getNewArmorStand(location.clone().add(0, 0.3, 0.2), false, true);

    body.setHelmet(new ItemStack(Material.GOLD_BLOCK));

    head.setHelmet(getSkull("TheKing"));

    leftFeather.setHeadPose(new EulerAngle(4.7123889804, 0, 1.5707963268));
    leftFeather.setHelmet(new ItemStack(Material.YELLOW_DYE));

    rightFeather.setHeadPose(new EulerAngle(4.7123889804, 0, 1.5707963268));
    rightFeather.setHelmet(new ItemStack(Material.YELLOW_DYE));

    leftFeet.setRightArmPose(new EulerAngle(0.2617993878, 0, 0));
    leftFeet.setItemInHand(new ItemStack(Material.RED_SANDSTONE));

    rightFeet.setRightArmPose(new EulerAngle(0.2617993878, 0, 0));
    rightFeet.setItemInHand(new ItemStack(Material.RED_SANDSTONE));

    parts.put("body", body);
    parts.put("head", head);
    parts.put("leftfeather", leftFeather);
    parts.put("rightfeather", rightFeather);
    parts.put("leftfeet", leftFeet);
    parts.put("rightfeet", rightFeet);

  }

  private ItemStack getSkull(String duck) {
    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    ItemMeta itemMeta = item.getItemMeta();
    ((SkullMeta) itemMeta).setOwner(duck);
    item.setItemMeta(itemMeta);
    return item;
  }

  private ArmorStand getNewArmorStand(Location location, boolean visible, boolean mini) {
    ArmorStand as = location.getWorld().spawn(location, ArmorStand.class);

    as.setBasePlate(false);
    as.setArms(true);
    as.setVisible(visible);
    as.setInvulnerable(true);
    as.setCanPickupItems(false);
    as.setGravity(false);
    as.setSmall(mini);

    return as;
  }

  public void fly() {
    new BukkitRunnable() {
      public void run() {
        for (String part : parts.keySet()) {
          parts.get(part).teleport(parts.get(part).getLocation().add(0, 0.1, 0));
        }
      }
    }.runTaskTimer(TEST.getPlugin(), 0L, 1L);
  }
}
