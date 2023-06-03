package github.rainbowmori.test;

import github.rainbowmori.rainbowapi.RMPlugin;
import github.rainbowmori.rainbowapi.object.commandapi.CommandAPICommand;
import github.rainbowmori.test.block.LuckOreBlock;
import github.rainbowmori.test.item.HideAmpouleItem;
import github.rainbowmori.test.item.LuckOreItem;
import github.rainbowmori.test.item.OfroAlertItem;
import github.rainbowmori.test.item.OfroSonerItem;
import github.rainbowmori.test.item.TestItem;
import github.rainbowmori.test.materialshiop.MaterialShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class TEST extends RMPlugin {

  private static Economy econ = null;

  public static TEST getPlugin() {
    return getPlugin(TEST.class);
  }

  public static Economy getEconomy() {
    return econ;
  }

  @Override
  public void onEnable() {
    if (!setupEconomy()) {
      getPrefixUtil().logError(
          String.format("[%s] - Disabled due to no Vault dependency found!", getName()));
      getServer().getPluginManager().disablePlugin(this);
      return;
    }

    registerCommand(new CommandAPICommand("openshop").executesPlayer((sender, args) -> {
      MaterialShop.openShop(sender);
    }));

    registerItem("test", TestItem.class);
    registerItem("OfroAlertItem", OfroAlertItem.class);
    registerItem("OfroSonerItem", OfroSonerItem.class);
    registerItem("HideAmpouleItem", HideAmpouleItem.class);

    registerItem("LuckOreItem", LuckOreItem.class);
    registerBlock("LuckOreBlock", LuckOreBlock.class);

    registerEvent(new Events());
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
        .getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return true;
  }

  @Override
  public String getPrefix() {
    return "<gray>[<red>TEST<gray>]";
  }
}
