package world.kitpvp.slime.inventory;

import world.kitpvp.slime.api.inventory.VirtualInventoryView;

public interface PlayerVirtualInventoryView extends VirtualInventoryView {
    @Override
    default int virtualInventorySize() {
        return net.minecraft.world.entity.player.Inventory.INVENTORY_SIZE; // equipment and crafting slots are not available, even though returned by virtualInventory.getSize()
    }
}
