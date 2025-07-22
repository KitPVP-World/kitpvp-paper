package world.kitpvp.slime.inventory;

import net.kyori.adventure.text.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ContainerVirtualInventoryView extends CraftInventoryView<AbstractContainerMenu, Inventory> implements PlayerVirtualInventoryView {

    private final PlayerInventory virtualInventory;
    private @Nullable Component titleOverride;

    ContainerVirtualInventoryView(HumanEntity player, Inventory viewing, AbstractContainerMenu container, PlayerInventory virtualInventory) {
        super(player, viewing, container);
        this.virtualInventory = virtualInventory;
    }

    @Override
    public PlayerInventory getBottomInventory() {
        return this.virtualInventory;
    }

    @Override
    public void overrideTitle(@Nullable Component title) {
        this.titleOverride = title;
        PlayerVirtualInventoryView.super.overrideTitle(title);
    }

    // taken from CraftContainer constructor, added titleOverride
    @Override
    public net.kyori.adventure.text.Component title() {
        return this.titleOverride != null ? this.titleOverride : this.getTopInventory() instanceof CraftInventoryCustom custom ? custom.title() : getTopInventory().getType().defaultTitle();
    }
    // end
}
