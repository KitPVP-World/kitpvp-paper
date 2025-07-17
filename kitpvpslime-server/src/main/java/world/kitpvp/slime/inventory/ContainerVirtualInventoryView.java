package world.kitpvp.slime.inventory;

import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ContainerVirtualInventoryView extends CraftInventoryView<AbstractContainerMenu, Inventory> implements PlayerVirtualInventoryView {

    private final PlayerInventory virtualInventory;

    // taken from CraftContainer constructor
    private final String originalTitle = this.getTopInventory() instanceof CraftInventoryCustom custom ? custom.getTitle() : this.getTopInventory().getType().getDefaultTitle();
    private String title = this.originalTitle;
    // end

    ContainerVirtualInventoryView(HumanEntity player, Inventory viewing, AbstractContainerMenu container, PlayerInventory virtualInventory) {
        super(player, viewing, container);
        this.virtualInventory = virtualInventory;
    }

    @Override
    public PlayerInventory getBottomInventory() {
        return this.virtualInventory;
    }

    // taken from CraftContainer constructor
    @Override
    public net.kyori.adventure.text.Component title() {
        return this.getTopInventory() instanceof CraftInventoryCustom custom ? custom.title() : getTopInventory().getType().defaultTitle();
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getOriginalTitle() {
        return this.originalTitle;
    }

    @Override
    public void setTitle(String title) {
        CraftInventoryView.sendInventoryTitleChange(this, title);
        this.title = title;
    }
    // end
}
