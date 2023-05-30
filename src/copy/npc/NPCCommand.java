package github.rainbowmori.test;

import github.rainbowmori.rainbowapi.object.commandapi.CommandAPICommand;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.Argument;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.ArgumentSuggestions;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.ChatColorArgument;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.CustomArgument;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.CustomArgument.CustomArgumentException;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.CustomArgument.MessageBuilder;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.GreedyStringArgument;
import github.rainbowmori.rainbowapi.object.commandapi.arguments.StringArgument;
import github.rainbowmori.rainbowapi.util.ItemBuilder;
import github.rainbowmori.rainbowapi.util.Util;
import github.rainbowmori.test.npc.ChatNPC;
import github.rainbowmori.test.npc.NPC;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCCommand {

  private static final Map<UUID, String> npcSelected = new HashMap<>();

  private static String getSelect(Player player) {
    return npcSelected.get(player.getUniqueId());
  }

  private static boolean hasSelect(Player player) {
    return npcSelected.containsKey(player.getUniqueId());
  }

  public Command() {
    /*
    * copy
    * equipment (これはinventoryでやるかGuilibで)
    * */
    new CommandAPICommand("npc")
        .withSubcommand(new CommandAPICommand("create")
            .withArguments(getStringInput("id"))
            .executesPlayer((sender, args) -> {
              new ChatNPC(sender.getLocation(), args[0].toString());
              npcSelected.put(sender.getUniqueId(), args[0].toString());
            })
        )
        .withSubcommand(new CommandAPICommand("tphere")
            .executesPlayer((sender, args) -> {
              NPC.getNPC(getSelect(sender)).teleport(sender.getLocation());
              Util.send(sender,"ここにNPCをtpさせました");
            })
        )
        .withSubcommand(new CommandAPICommand("tp")
            .executesPlayer((sender, args) -> {
              sender.teleport(NPC.getNPC(getSelect(sender)).getLocation());
              Util.send(sender,"NPCにteleportしました");
            })
        )
        .withSubcommand(new CommandAPICommand("tpto")
            .executesPlayer((sender, args) -> {
              NPC.getNPC(getSelect(sender)).showDisplayName();
              Util.send(sender,"名前を表示しました");
            })
        )
        .withSubcommand(new CommandAPICommand("name")
            .withArguments(new GreedyStringArgument("displayName"))
            .executesPlayer((sender, args) -> {
              NPC npc = NPC.getNPC(getSelect(sender));
              Component component = Util.component(args[0].toString());
              String str = Util.serialize(component);
              npc.setDisplayName(component);
              npc.showDisplayName();
              System.out.println(str);
              Util.send(sender,str + "に名前を変更しました");
            })
        )
        .withSubcommand(new CommandAPICommand("showname")
            .executesPlayer((sender, args) -> {
              NPC.getNPC(getSelect(sender)).showDisplayName();
              Util.send(sender,"名前を表示しました");
            })
        )
        .withSubcommand(new CommandAPICommand("hidename")
            .executesPlayer((sender, args) -> {
              NPC.getNPC(getSelect(sender)).hideDisplayName();
              Util.send(sender,"名前を隠しました");
            })
        )
        .withSubcommand(new CommandAPICommand("selection")
            .withArguments(getStringInput("id").replaceSuggestions(ArgumentSuggestions.strings(
                NPC.getNPC().keySet())))
            .executesPlayer((sender, args) -> {
              npcSelected.put(sender.getUniqueId(), args[0].toString());
              Util.send(sender,args[0] + "を選択しました");
            })
        )
        .withSubcommand(new CommandAPICommand("show")
            .executesPlayer((sender, args) -> {
              Util.send(sender,"表示しました");
              NPC.getNPC(getSelect(sender)).show();
            }))
        .withSubcommand(new CommandAPICommand("hide")
            .executesPlayer((sender, args) -> {
              Util.send(sender,"隠しました");
              NPC.getNPC(getSelect(sender)).hide();
            }))
        .withSubcommand(new CommandAPICommand("list")
            .executesPlayer((sender, args) -> {
              Util.send(sender,"=====LIST=====");
              NPC.getNPC().keySet().forEach(s -> {
                Util.send(sender,s);
              });
            })
        )
        .withSubcommand(new CommandAPICommand("glow")
            .executesPlayer((sender, args) -> {
              Util.send(sender,"光らせました");
              NPC.getNPC(getSelect(sender)).glow();
            }))
        .withSubcommand(new CommandAPICommand("glow")
            .withArguments(new ChatColorArgument("color"))
            .executesPlayer((sender, args) -> {
              Util.send(sender,Util.toAndChatColor(args[0].toString() + "に光らせました"));
              NPC.getNPC(getSelect(sender)).glow(((ChatColor) args[0]));
            }))
        .withSubcommand(new CommandAPICommand("unglow")
            .executesPlayer((sender, args) -> {
              Util.send(sender,"光るのを止めました");
              NPC.getNPC(getSelect(sender)).unglow();
            }))
        .withSubcommand(new CommandAPICommand("remove")
            .executesPlayer((sender, args) -> {
              NPC.getNPC(getSelect(sender)).remove();
              Util.send(sender,getSelect(sender) + "を削除しました");
            }))
        .register();
  }

  private static Argument<String> getStringInput(String nodeName) {
    return new CustomArgument<>(new StringArgument(nodeName), info -> {
      if (info.input().length() > 16) {
        throw new CustomArgumentException(
            new MessageBuilder("17文字以上にしないでください: ").append(info.input().length()));
      }
      return info.input();
    });
  }
}
