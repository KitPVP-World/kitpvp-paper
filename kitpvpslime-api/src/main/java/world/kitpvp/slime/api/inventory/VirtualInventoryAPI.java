package world.kitpvp.slime.api.inventory;

import net.kyori.adventure.util.Services;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Virtual inventory API for showing a different player inventory until the player closes it.
 * <br>
 * For an example implementation see: <a href="https://github.com/KitPVP-World/kitpvp-paper/tree/main/java/world/kitpvp/testplugin/VirtualViewTest.java">VirtualViewTest</a>
 */
@NullMarked
public interface VirtualInventoryAPI {
    /**
     * Create the virtual inventory for a player. Use {{ {@link VirtualInventoryView#open()} }} to show it then.
     * @param player player to show
     * @param topInventory standard inventory to show
     */
    VirtualInventoryView createVirtualInventory(Player player, Inventory topInventory);

    /**
     * Gets the instance of the AdvancedSlimePaper API.
     *
     * @return the instance of the AdvancedSlimePaper API
     */
    static VirtualInventoryAPI instance() {
        return Holder.INSTANCE;
    }

    @ApiStatus.Internal
    class Holder {
        private static final VirtualInventoryAPI INSTANCE = Services.service(VirtualInventoryAPI.class).orElseThrow();
    }
}
