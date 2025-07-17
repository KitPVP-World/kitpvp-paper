package world.kitpvp.testplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import world.kitpvp.testplugin.command.NametagCommand;
import world.kitpvp.testplugin.inventory.InventoryHandler;

public class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new InventoryHandler(), this);
        this.getServer().getPluginManager().registerEvents(new NametagCommand(), this);
    }

    @EventHandler
    public void deathHandler(PlayerDeathEvent event) {
        event.cancelRespawnScreen();
    }


//    @EventHandler
//    public void inventoryClickHandler(InventoryClickEvent event) {
//
//        event.getInventory().getHolder();
//
//        System.out.println("Clicked " + event.getCurrentItem().getType().key() );
//        System.out.println("Slot " + event.getSlot() + " clicked." + " - " + event.getClickedInventory().getType().name());
//        if (event.getClickedInventory() != event.getView().getBottomInventory()) {
//            return;
//        }
//
//        if (event.getSlot() < 9) {
//            if(event.getSlot() == 5) {
//                Inventory topInventory = Bukkit.createInventory(null, 9*3, Component.text("Another inventory").color(NamedTextColor.RED));
////                        topInventory.setItem(10, ItemStack.of(Material.DIRT, 5));
//
//                VirtualInventoryView virtualView = VirtualInventoryAPI.instance().createVirtualInventory((Player) event.getWhoClicked(), topInventory);
////                        PlayerInventory bottomInventory = virtualView.getBottomInventory();
//
////                        bottomInventory.setItemInMainHand(ItemStack.of(Material.DIAMOND_SWORD).enchantWithLevels(30, false, new Random()));
////
//                for(int i = 0; i < 63; i++) {
//                    virtualView.setItem(i, ItemStack.of(Material.DIRT, i % 64 + 1));
//                }
//                virtualView.open();
//
//
//            }
//
//            event.setCancelled(true);
//        }
//    }
}
