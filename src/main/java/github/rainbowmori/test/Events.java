package github.rainbowmori.test;

import github.rainbowmori.rainbowapi.object.customblock.CustomBlock;
import github.rainbowmori.rainbowapi.util.Util;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

  @EventHandler
  public void breakOre(BlockBreakEvent e) {
    Block block = e.getBlock();
    if (block.getType().name().contains("_ORE")) {
      Player player = e.getPlayer();
      UUID uuid = player.getUniqueId();
      if (CustomBlock.blockCache.computeGetCache(uuid,new EffectBlock(uuid)).hasLuckOre) {
        e.setDropItems(false);
        Collection<ItemStack> drops = block.getDrops(
            player.getInventory().getItemInMainHand(), player);
        drops.forEach(itemStack -> {
          itemStack.setAmount(itemStack.getAmount() * 2);
          block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
        });
        Util.send(player,"<yellow>あなたは幸運です！鉱石が二倍に増えました");
      }
    }
  }
}
