package world.kitpvp.slime.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.BoundingBox;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A sync event for modifying the target location of an item with the "RandomTeleport" consumable effect.
 */
@NullMarked
public class TeleportRandomlyConsumableLocationChooseEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final LivingEntity entity;

    private @Nullable BoundingBox boundingBox;
    private boolean cancelled;

    public TeleportRandomlyConsumableLocationChooseEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public @Nullable BoundingBox boundingBox() {
        return this.boundingBox != null ? this.boundingBox.clone() : null;
    }

    public void boundingBox(@Nullable BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public LivingEntity entity() {
        return this.entity;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
