package world.kitpvp.testplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import world.kitpvp.testplugin.command.NametagCommand;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new NametagCommand(), this);
    }
    @EventHandler
    public void deathHandler(PlayerDeathEvent event) {
        event.cancelRespawnScreen();
    }
}
