package world.kitpvp.testplugin.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryHandler implements Listener {

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().getHolder() instanceof StandardInventory holder)
            holder.handleClick(event);
    }

}
