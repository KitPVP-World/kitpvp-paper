package world.kitpvp.slime.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * A sync event that is called whenever an entity's fall distance is reset after being increased
 * @see Entity#getFallDistance()
 */
@NullMarked
public class EntityFallEvent extends EntityEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public EntityFallEvent(Entity entity) {
        super(entity);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
