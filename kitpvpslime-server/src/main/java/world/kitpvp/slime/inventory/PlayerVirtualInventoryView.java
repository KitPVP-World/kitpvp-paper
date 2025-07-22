package world.kitpvp.slime.inventory;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.jspecify.annotations.Nullable;
import world.kitpvp.slime.api.inventory.VirtualInventoryView;

import java.util.List;

public interface PlayerVirtualInventoryView extends VirtualInventoryView {
    @Override
    default int virtualInventorySize() {
        return net.minecraft.world.entity.player.Inventory.INVENTORY_SIZE; // equipment and crafting slots are not available, even though returned by virtualInventory.getSize()
    }

    @Override
    default void overrideTitle(@Nullable Component title) {
        final ServerPlayer player = (ServerPlayer) ((CraftHumanEntity) this.getPlayer()).getHandle();
        if(!player.isInventoryAllowedToUpdate(((CraftInventoryPlayer) this.getBottomInventory()).getInventory())) {
            return;
        }

        final int containerId = player.containerMenu.containerId;
        final net.minecraft.world.inventory.MenuType<?> windowType = CraftContainer.getNotchInventoryType(this.getTopInventory());
        player.connection.send(new ClientboundOpenScreenPacket(containerId, windowType, PaperAdventure.asVanilla(this.title())));

        List<ItemStack> items = ((CraftInventory) getTopInventory()).getInventory().getContents();
        player.containerSynchronizer.sendInitialData(player.containerMenu, items, player.containerMenu.getCarried().copy(), player.containerMenu.remoteDataSlots.toIntArray());

//        player.containerMenu.sendAllDataToRemote();
    }
}
