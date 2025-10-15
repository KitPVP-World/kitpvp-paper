package world.kitpvp.testplugin.command;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import world.kitpvp.slime.nametag.CustomName;

public class NametagCommand implements Listener {
    private static CustomName name;

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("nametag")
                .executes((context) -> {
                    if (NametagCommand.name != null) {
                        NametagCommand.name.setHidden(true);

                        NametagCommand.name = null;
                    }

                    Entity entity = context.getSource().getExecutor();
                    if (entity == null) {
                        context.getSource().getSender().sendRichMessage("<red>No executing entity.");
                        return 0;
                    }

                    CustomName name = new CustomName(entity);
                    name.setName(MiniMessage.miniMessage().deserialize("""
                            <dark_gray>[<gradient:#5CFFFF:#8AFFFF:0.1>53★</gradient><dark_gray>]
                            <red><username></red>""",
                        TagResolver.resolver("username", Tag.inserting(entity.name()))
                    ));
                    name.setHidden(false);
                    NametagCommand.name = name;

                    return Command.SINGLE_SUCCESS;
                })
                .build()
        );
    }

    @EventHandler
    public void handleMove(PlayerQuitEvent event) {
        if (name == null)
            return;
        if (event.getPlayer() != name.getTargetEntity())
            return;
        name.setHidden(true);
        name = null;
    }

    @EventHandler
    public void handleSneak(PlayerToggleSneakEvent event) {
        if (name == null)
            return;
        if (event.getPlayer() != name.getTargetEntity())
            return;
        name.setTargetEntitySneaking(event.isSneaking());
    }


}
