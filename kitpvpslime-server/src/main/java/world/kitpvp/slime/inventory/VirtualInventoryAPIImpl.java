package world.kitpvp.slime.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerEquipment;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.NautilusInventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.bukkit.craftbukkit.entity.CraftAbstractHorse;
import org.bukkit.craftbukkit.entity.CraftAbstractNautilus;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.craftbukkit.inventory.CraftInventoryLectern;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventorySaddledMount;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import world.kitpvp.slime.api.inventory.VirtualInventoryAPI;
import world.kitpvp.slime.api.inventory.VirtualInventoryView;

@NullMarked
public class VirtualInventoryAPIImpl implements VirtualInventoryAPI {
    @Override
    public VirtualInventoryView createVirtualInventory(Player player, org.bukkit.inventory.Inventory topInventory) {
        ServerPlayer playerHandle = ((CraftPlayer) player).getHandle();

        Inventory virtualInventoryHandle = new net.minecraft.world.entity.player.Inventory(playerHandle, new PlayerEquipment(playerHandle));
        CraftInventoryPlayer virtualInventory = new CraftInventoryPlayer(virtualInventoryHandle);

        return provideView(player, playerHandle, topInventory, virtualInventoryHandle, virtualInventory);
    }

    private VirtualInventoryView provideView(Player player, ServerPlayer playerHandle, org.bukkit.inventory.Inventory inventory, Inventory virtualInventoryHandle, CraftInventoryPlayer virtualInventory) {
        // extracted and modified from org.bukkit.craftbukkit.entity.CraftHumanEntity.openInventory(org.bukkit.inventory.Inventory)

        MenuProvider menuProvider = null;
        if (inventory instanceof CraftInventoryDoubleChest) {
            menuProvider = ((CraftInventoryDoubleChest) inventory).provider;
        } else if (inventory instanceof CraftInventoryLectern) {
            menuProvider = ((CraftInventoryLectern) inventory).provider;
        } else if (inventory instanceof CraftInventory craft && craft.getInventory() instanceof MenuProvider provider) {
            menuProvider = provider;
        }

        if (menuProvider instanceof final BlockEntity blockEntity && !blockEntity.hasLevel()) {
            blockEntity.setLevel(playerHandle.level());
        }

        AbstractContainerMenu abstractContainerMenu;
        if (menuProvider != null) {
            abstractContainerMenu = menuProvider.createMenu(playerHandle.nextContainerCounter(), virtualInventoryHandle, playerHandle);
            if (abstractContainerMenu != null) {
                abstractContainerMenu.setTitle(menuProvider.getDisplayName());
            }
            throw new IllegalStateException("Cannot create menu container for " + menuProvider);
        } else if (inventory instanceof CraftInventorySaddledMount craft && craft.getInventory().getOwner() instanceof CraftAbstractHorse horse) {
            abstractContainerMenu = new HorseInventoryMenu(
                    playerHandle.nextContainerCounter(),
                    virtualInventoryHandle,
                    craft.getInventory(),
                    horse.getHandle(),
                    horse.getHandle().getInventoryColumns()
            );
            abstractContainerMenu.setTitle(horse.getHandle().getDisplayName());
        } else if (inventory instanceof CraftInventorySaddledMount craft && craft.getInventory().getOwner() instanceof CraftAbstractNautilus nautilus) {
            abstractContainerMenu = new NautilusInventoryMenu(
                    playerHandle.nextContainerCounter(),
                    virtualInventoryHandle,
                    craft.getInventory(),
                    nautilus.getHandle(),
                    nautilus.getHandle().getInventoryColumns()
            );
            abstractContainerMenu.setTitle(nautilus.getHandle().getDisplayName());
        } else {
            return new CustomVirtualInventoryView(player, inventory, virtualInventory);
        }

        return new ContainerVirtualInventoryView(player, inventory, abstractContainerMenu, virtualInventory);
    }

}
