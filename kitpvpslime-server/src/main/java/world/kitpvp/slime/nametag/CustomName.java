package world.kitpvp.slime.nametag;

import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import world.kitpvp.slime.InternalPluginInstance;

import java.util.function.Consumer;

@NullMarked
public class CustomName {

    private final SkeletonTextDisplay interaction = new SkeletonTextDisplay(this);

    // Target entity constants
    private final Vector3f effectiveTransiton;
    private final Vector3f passengerOffset;
    private final Entity targetEntity;

    // Custom name constants
    private final int nametagEntityId;

    // States
    private boolean targetEntitySneaking;
    private Component name = Component.empty();
    private boolean hidden;

    private final BukkitTask task;

    public CustomName(Entity entity) {
        this.nametagEntityId = net.minecraft.world.entity.Entity.nextEntityId();
        this.targetEntity = entity;

        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        Vec3 ridingOffset = nmsEntity.getAttachments().getClamped(EntityAttachment.PASSENGER, 0, nmsEntity.getYRot());
        Vec3 nametagOffset = nmsEntity.getAttachments().get(EntityAttachment.NAME_TAG, 0, nmsEntity.getYRot());

        this.passengerOffset = ridingOffset.toVector3f();
        // First, negate the riding offset to get to the bounding of the entity's bounding box
        // Add the default offset of nametags to imitate it using the text displaying correctly (0.3)
        this.effectiveTransiton = nametagOffset.add(0, -ridingOffset.y + 0.3, 0).toVector3f();

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                Packet<ClientGamePacketListener> riderPacket = CustomName.this.interaction.getRiderPacket();
                for (Player player : entity.getTrackedBy()) {
                    ((CraftPlayer) player).getHandle().connection.send(riderPacket);
                }
            }
        }.runTaskTimer(InternalPluginInstance.INSTANCE, 20, 20);
    }

    public void setName(Component name) {
        this.name = name;
        this.syncData();
    }

    public void setTargetEntitySneaking(boolean targetEntitySneaking) {
        this.targetEntitySneaking = targetEntitySneaking;
        this.syncData();
    }

    public void sendToClient(@NotNull Player entity) {
        if (this.hidden) {
            return;
        }

        ((CraftPlayer) entity).getHandle().connection.send(this.interaction.initialSpawnPacket());
    }

    public void removeFromClient(@NotNull Player entity) {
        ((CraftPlayer) entity).getHandle().connection.send(this.interaction.removePacket());
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        this.runOnTrackers((player) -> {
            if (hidden) {
                this.removeFromClient(player);
            } else {
                this.sendToClient(player);
            }
        });
    }

    public Component getName() {
        return this.name;
    }

    public int getNametagId() {
        return this.nametagEntityId;
    }

    public Entity getTargetEntity() {
        return this.targetEntity;
    }

    public boolean isTargetEntitySneaking() {
        return this.targetEntitySneaking;
    }

    public Vector3f getEffectiveTransition() {
        return this.effectiveTransiton;
    }

    public Vector3f getPassengerOffset() {
        return this.passengerOffset;
    }

    // Utilities
    private void syncData() {
        if (this.hidden) {
            return;
        }

        Packet<ClientGamePacketListener> dataPacket = this.interaction.syncDataPacket();
        this.runOnTrackers((player) -> ((CraftPlayer) player).getHandle().connection.send(dataPacket));
    }

    private void runOnTrackers(Consumer<Player> consumer) {
        for (Player player : this.targetEntity.getTrackedBy()) {
            consumer.accept(player);
        }
    }

    public void close() {
        this.task.cancel();
    }

    public boolean isHidden() {
        return this.hidden;
    }

}
