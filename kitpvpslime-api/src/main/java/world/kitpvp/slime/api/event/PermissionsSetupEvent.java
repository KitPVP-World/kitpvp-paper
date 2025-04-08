package world.kitpvp.slime.api.event;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissibleBase;
import org.jetbrains.annotations.NotNull;

/**
 * A sync event for setting a player's {@link PermissibleBase}.
 * It is called right before the {@link org.bukkit.event.player.PlayerLoginEvent}
 */
public class PermissionsSetupEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private PermissibleBase permissibleBase = null;
    private final HumanEntity player;

    public PermissionsSetupEvent(@NotNull HumanEntity player) {
        this.player = player;
    }

    @NotNull
    public PermissibleBase permissibleBase() {
        if(this.permissibleBase == null) {
            this.permissibleBase = new PermissibleBase(this.player);
        }
        return this.permissibleBase;
    }


    public void permissibleBase(@NotNull PermissibleBase permissibleBase) {
        this.permissibleBase = permissibleBase;
    }

    @NotNull
    public HumanEntity player() {
        return this.player;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
