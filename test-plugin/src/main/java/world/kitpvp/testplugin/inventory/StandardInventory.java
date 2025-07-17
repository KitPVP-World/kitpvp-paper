package world.kitpvp.testplugin.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class StandardInventory implements InventoryHolder {

    private final Player player;
    private final Inventory inventory;

    public StandardInventory(Player player, Component title) {
        this.player = player;

        // creating top inventory
        this.inventory = this.createInventory(title);
    }

    public Inventory createInventory(Component title) {
        return this.player.getServer().createInventory(this, 9 * 3, title);
    }

    public abstract void handleClick(InventoryClickEvent event);

    public final Player player() {
        return this.player;
    }

    @Override
    public final @NotNull Inventory getInventory() {
        return this.inventory;
    }


}
