package world.kitpvp.testplugin;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.jetbrains.annotations.NotNull;
import world.kitpvp.testplugin.command.DisplayPlayerCommand;
import world.kitpvp.testplugin.command.InventoryCommand;
import world.kitpvp.testplugin.command.LoadWorldsTestCommand;
import world.kitpvp.testplugin.command.NametagCommand;
import world.kitpvp.testplugin.command.TestCommand;

public class TestPluginBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        var lifecycleManager = context.getLifecycleManager();
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();


            LoadWorldsTestCommand.register(commands, context.getDataDirectory());
            DisplayPlayerCommand.register(commands);
            TestCommand.register(commands);
            NametagCommand.register(commands);
            InventoryCommand.register(commands);
        });
    }
}
