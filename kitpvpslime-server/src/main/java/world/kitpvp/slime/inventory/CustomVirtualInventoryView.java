package world.kitpvp.slime.inventory;

import org.bukkit.craftbukkit.inventory.CraftAbstractInventoryView;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftMenuType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomVirtualInventoryView extends CraftAbstractInventoryView implements PlayerVirtualInventoryView {

    private final Player player;
    private final Inventory inventory;
    private final PlayerInventory virtualInventory;


    private final String originalTitle;
    private String title;

    public CustomVirtualInventoryView(Player player, Inventory inventory, PlayerInventory virtualInventory) {
        this.player = player;
        this.inventory = inventory;
        this.virtualInventory = virtualInventory;
        this.originalTitle = inventory instanceof CraftInventoryCustom ? ((CraftInventoryCustom) inventory).getTitle() : inventory.getType().getDefaultTitle(); // Paper
        this.title = this.originalTitle;
    }

    @Override
    public Inventory getTopInventory() {
        return this.inventory;
    }

    @Override
    public PlayerInventory getBottomInventory() {
        return this.virtualInventory;
    }

    @Override
    public HumanEntity getPlayer() {
        return this.player;
    }

    @Override
    public InventoryType getType() {
        return this.inventory.getType();
    }

    // Paper start
    @Override
    public net.kyori.adventure.text.Component title() {
        return this.inventory instanceof CraftInventoryCustom custom ? custom.title() : this.inventory.getType().defaultTitle(); // Paper
    }
    // Paper end

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

    @Override
    public MenuType getMenuType() {
        return CraftMenuType.minecraftToBukkit(CraftContainer.getNotchInventoryType(this.inventory));
    }

}
