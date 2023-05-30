package github.rainbowmori.test.npc;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RightClickNPC extends Event implements Cancellable {

  private final Player player;
  private final NPC npc;
  private static final HandlerList HANDLERS_LIST = new HandlerList();
  private boolean isCancelled;

  public RightClickNPC(Player player,NPC npc){
    this.player = player;
    this.npc = npc;
    this.isCancelled = false;
  }

  @Override
  public boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.isCancelled = cancelled;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLERS_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLERS_LIST;
  }

  public Player getPlayer() {
    return player;
  }

  public NPC getNpc() {
    return npc;
  }
}