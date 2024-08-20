package world.kitpvp.slime.test;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import world.kitpvp.slime.test.commands.*;

public class TestPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        var lifecycleManager = this.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            LoadWorldsTestCommand.register(commands);
        });
    }
}
