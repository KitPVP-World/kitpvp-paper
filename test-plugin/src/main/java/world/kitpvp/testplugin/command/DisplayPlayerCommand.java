package world.kitpvp.testplugin.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import world.kitpvp.slime.api.entity.DisplayPlayer;


public class DisplayPlayerCommand {

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("player")
                .then(Commands.argument("name", StringArgumentType.word())
                    .then(Commands.literal("spawn")
                        .executes((context) -> {
                            String name = context.getArgument("name", String.class);
                            DisplayPlayer player = context.getSource().getLocation().getWorld().spawn(context.getSource().getLocation(), DisplayPlayer.class);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 0, false, false));

                            ((Player) context.getSource().getSender()).setSpectatorTarget(player);
                            context.getSource().getSender().sendRichMessage("<yellow>Spawned " + name);


                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
                .build()
        );
    }

}

