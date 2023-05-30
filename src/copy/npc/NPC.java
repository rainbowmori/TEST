package github.rainbowmori.test.npc;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.gson.JsonObject;
import github.rainbowmori.rainbowapi.RainbowAPI;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.TEST;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public abstract class NPC {

  public static final NamespacedKey key = new NamespacedKey(TEST.getPlugin(), "NPC");

  public static boolean isNPC(Entity entity) {
    return entity.getPersistentDataContainer().has(key);
  }

  private static final Map<String, NPC> NPC = new HashMap<>();

  protected final Entity entity;

  protected final String id;

  protected Component displayName;

  public NPC(Location location, String id) {
    this(location.getWorld().spawn(location, Villager.class, mob -> {
      mob.setAI(false);
      mob.setSilent(true);
      mob.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
    }), id);
  }

  public NPC(Entity entity,String id) {
    this.entity = entity;
    this.id = id;
    this.displayName = Util.mm(id);
    NPC.put(id, this);
  }

  public static Map<String, NPC> getNPC() {
    return NPC;
  }

  public static NPC getNPC(String id) {
    return NPC.get(id);
  }

  public static void loadNPC(JsonObject object) {
    switch (object.get("NPCType").getAsString()) {
      case "ChatNPC" -> ChatNPC.loadNPC(object);
      default -> {
        TEST.getRainbowAPI().prefixUtil.logError(object.get("NPCType").getAsString() + "は有効なNPCとして作られていません");
        //これはRainbowAPIに移して NPC.addNPC(String NPCType,Class<T>)としてregisterできるようにするか (Tは<T exetends NPC>のclass)
        //https://www.zunouissiki.com/java-reflection-staticmethod-accessible/
      }
    }
  }

  public abstract String getNPCType();

  public void setDisplayName(boolean bool) {
    if (bool) {
      showDisplayName();
    } else {
      hideDisplayName();
    }
  }

  public void setDisplayName(Component component) {
    this.displayName = component;
    if (getEntity().isCustomNameVisible()) {
      getEntity().customName(this.displayName);
    }
  }

  public void teleport(Location location) {
    getEntity().teleport(location);
  }

  public Location getLocation() {
    return getEntity().getLocation();
  }

  public void showDisplayName() {
    getEntity().customName(this.displayName);
    getEntity().setCustomNameVisible(true);
  }

  public void hideDisplayName() {
    getEntity().customName(null);
    getEntity().setCustomNameVisible(false);
  }

  public void remove() {
    getEntity().remove();
    getNPC().remove(id);
  }

  public void glow(ChatColor color) {
    players(player -> {
      try {
        RainbowAPI.getGlowAPI().setGlowing(getEntity(), player, color);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void glow() {
    players(player -> {
      try {
        RainbowAPI.getGlowAPI().setGlowing(getEntity(), player);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void unglow() {
    players(player -> {
      try {
        RainbowAPI.getGlowAPI().unsetGlowing(getEntity(), player);
      } catch (ReflectiveOperationException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("NPCType", getNPCType());
    json.addProperty("entityType",getEntity().getType().getEntityClass().getSimpleName());
    json.addProperty("displayName", Util.serialize(displayName));
    json.addProperty("id", id);
    json.addProperty("uuid", getEntity().getUniqueId().toString());
    json.addProperty("showName", getEntity().isCustomNameVisible());
    json.add("location", RainbowAPI.gson.toJsonTree(getEntity().getLocation()));
    return json;
  }

  public Entity getEntity() {
    return entity;
  }

  public void show() {
    players(player -> player.showEntity(TEST.getPlugin(),getEntity()));
  }

  public void hide() {
    players(player -> player.hideEntity(TEST.getPlugin(),getEntity()));
  }

  public void sendsPacket(PacketContainer packet) {
    players(player -> sendPacket(player, packet));
  }

  public void sendPacket(Player player, PacketContainer packet) {
    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public void players(Consumer<Player> consumer) {
    Bukkit.getOnlinePlayers().forEach(consumer);
  }

  public abstract void rightClick(Player player);
  //  public abstract void leftClick(Player player);
}
