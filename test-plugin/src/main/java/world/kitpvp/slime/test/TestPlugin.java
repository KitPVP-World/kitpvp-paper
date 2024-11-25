package world.kitpvp.slime.test;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import world.kitpvp.paper.api.event.PermissionsSetupEvent;
import world.kitpvp.slime.test.commands.*;

public class TestPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        var lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            LoadWorldsTestCommand.register(commands);
            PermissionCommand.register(this, commands);
        });

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void handlePermissions(PermissionsSetupEvent event) {
        System.out.println("Handle Permissions");
        event.permissibleBase(new TestPermissibleBase(event.player()));
    }
}
