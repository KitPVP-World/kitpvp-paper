package world.kitpvp.testplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);}
    @EventHandler
    public void deathHandler(PlayerDeathEvent event) {
        event.cancelRespawnScreen();
    }
}
