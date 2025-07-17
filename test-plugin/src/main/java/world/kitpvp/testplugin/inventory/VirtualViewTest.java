package world.kitpvp.testplugin.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import world.kitpvp.slime.api.inventory.VirtualInventoryAPI;
import world.kitpvp.slime.api.inventory.VirtualInventoryView;

public abstract class VirtualViewTest extends StandardInventory {
    private final Runnable[] actions;

    private final VirtualInventoryView virtualView;

    public VirtualViewTest(Player player, Component title) {
        super(player, title);

        // creating the new virtual inventory
        this.virtualView = VirtualInventoryAPI.instance().createVirtualInventory(player, this.getInventory());

        // preparing click actions
        this.actions = new Runnable[this.virtualView.fullSize()];
    }

    public int topSlot(int slot) {
        return this.virtualView.rawSlotInTopInventory(slot);
    }

    public int bottomSlot(int slot) {
        return this.virtualView.rawSlotInVirtualInventory(slot);
    }

    public void button(int slot, ItemStack item, Runnable action) {
        this.virtualView.setItem(slot, item);
        this.actions[slot] = action;
    }

    public VirtualInventoryView virtualView() {
        return this.virtualView;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        // the event.getView() will never be the virtualView instance
        // because that would break the custom inventory views by the Bukkit API, i.e. for furnaces
        assert event.getView() != this.virtualView;
        if(event.getView() ==  this.virtualView) {
            System.out.println("Event view is virtual view");
        }
        System.out.println(event.getView().getClass().getCanonicalName());
        System.out.println(this.virtualView.getClass().getCanonicalName());

        // disable moving items both in the virtual and top inventory
        event.setCancelled(true);

        if(event.getRawSlot() < 0 || event.getRawSlot() >= this.actions.length) {
            return;
        }

        Runnable runnable = this.actions[event.getRawSlot()];
        if(runnable != null) {
            runnable.run();
        }
    }

    public void open() {
        this.virtualView.open();
    }


}
