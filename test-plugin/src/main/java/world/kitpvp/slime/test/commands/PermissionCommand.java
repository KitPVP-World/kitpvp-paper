package world.kitpvp.slime.test.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.entity.Player;
import world.kitpvp.slime.test.TestPlugin;

import java.util.HashMap;
import java.util.UUID;

public class PermissionCommand {

    private static final HashMap<UUID, PermissionAttachment> ATTACHMENTS = new HashMap<>();

    public static void register(TestPlugin plugin, Commands commands) {
        commands.register(
            Commands.literal("permission")
                .then(Commands.argument("permission", StringArgumentType.word())
                    .executes((context) -> {
                        var permission = context.getArgument("permission", String.class);
                        var attachment = getAttachment(plugin, (Player) context.getSource().getSender());
                        attachment.setPermission(permission, true);

                        return Command.SINGLE_SUCCESS;
                    })).build());
    }

    private static PermissionAttachment getAttachment(TestPlugin plugin, Player player) {
        return ATTACHMENTS.computeIfAbsent(player.getUniqueId(), (uuid) -> player.addAttachment(plugin));
    }

}
