package world.kitpvp.slime.api.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissibleBase;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A sync event for setting a player's {@link PermissibleBase}.
 * It is called when a {@link HumanEntity} is initialized.
 */
@NullMarked
public class PermissionsSetupEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private @Nullable PermissibleBase permissibleBase = null;
    private final HumanEntity player;

    public PermissionsSetupEvent(HumanEntity player) {
        this.player = player;
    }

    public PermissibleBase permissibleBase() {
        if(this.permissibleBase == null) {
            this.permissibleBase = new PermissibleBase(this.player);
        }
        return this.permissibleBase;
    }

    public void permissibleBase(PermissibleBase permissibleBase) {
        this.permissibleBase = permissibleBase;
    }

    public HumanEntity player() {
        return this.player;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
