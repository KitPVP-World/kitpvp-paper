package world.kitpvp.slime.api.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

/**
 * An inventory view that shows the player a different hotbar and inventory contents
 * @apiNote This view instance may not be returned {@link InventoryEvent#getView()}.
 * You should create a custom {@link org.bukkit.inventory.InventoryHolder} on the top inventory to differentiate between the inventories, <a href="https://docs.papermc.io/paper/dev/custom-inventory-holder/">see PaperMC docs</a>.
 * In your InventoryHolder you can also store the bottom player inventory returned by {@link VirtualInventoryView} since you cannot use {@link Player#getInventory()} to get the virtual inventory.
 */
@NullMarked
public interface VirtualInventoryView extends InventoryView {

    @Override
    PlayerInventory getBottomInventory();

    /**
     * Provides the raw slot id of a slot in the top inventory
     * @param slot slot relative to the top inventory's first slot
     * @return the slot (-> this would not be necessary)
     */
    default int rawSlotInTopInventory(@Range(from = 0, to = Short.MAX_VALUE) int slot) {
        return slot;
    }

    /**
     * Provides the raw slot id of a slot in the bottom inventory
     * @param slot slot relative to the bottom inventory's first slot
     * @return the top inventory's last slot plus the slot
     */
    default int rawSlotInVirtualInventory(@Range(from = 0, to = Short.MAX_VALUE) int slot) {
        return this.getTopInventory().getSize() + slot;
    }

    /**
     * Overrides the title and sends a ClientboundOpenScreenPacket if the player has this view open
     */
    void overrideTitle(@Nullable Component title);

    /**
     * Provides the virtual inventory's size
     */
    int virtualInventorySize();

    /**
     * Provides the top inventory plus the virtual inventory's size
     */
    default int fullSize() {
        return this.getTopInventory().getSize() + this.virtualInventorySize();
    }


}
