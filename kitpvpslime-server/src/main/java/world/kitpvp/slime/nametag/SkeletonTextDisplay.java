package world.kitpvp.slime.nametag;

import io.netty.buffer.Unpooled;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

/**
 * This class is used for sending packets related to the text display entity
 * sent to the client.
 */
@NullMarked
public class SkeletonTextDisplay {

    private final CustomName customName;

    public SkeletonTextDisplay(CustomName customName) {
        this.customName = customName;
    }

    public Packet<ClientGamePacketListener> removePacket() {
        return new ClientboundRemoveEntitiesPacket(this.customName.getNametagId());
    }

    public Packet<ClientGamePacketListener> syncDataPacket(Player target) {
        byte styleFlags = 0;
        if (this.customName.isTargetEntitySneaking())
            styleFlags |= Display.TextDisplay.FLAG_SEE_THROUGH;

        var data = List.of(
            ofData(Display.TextDisplay.DATA_TEXT_ID, PaperAdventure.asVanilla(this.customName.getName(target))),
            ofData(Display.TextDisplay.DATA_STYLE_FLAGS_ID, styleFlags),
            ofData(Display.DATA_TRANSLATION_ID, this.customName.getEffectiveTransition()),
            ofData(Display.TextDisplay.DATA_TEXT_OPACITY_ID, (byte) (customName.isTargetEntitySneaking() ? 0x20 : 0xFF))
        );
        return new ClientboundSetEntityDataPacket(this.customName.getNametagId(), data);
    }

    public Packet<ClientGamePacketListener> getRiderPacket() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeVarInt(this.customName.getTargetEntity().getEntityId());

        int[] passengerIds = this.passengerIds();
        buf.writeVarIntArray(passengerIds);

        return new ClientboundSetPassengersPacket(buf);
    }

    private int[] passengerIds() {
        List<Entity> passengers = this.customName.getTargetEntity().getPassengers(); //respect passengers
        int[] passengerIds = new int[passengers.size()];
        if (!this.customName.isHidden()) {
            int length = passengerIds.length;
            passengerIds = new int[length + 1];
            passengerIds[length] = this.customName.getNametagId(); //always add the entity if it is visible
        }
        for (int i = 0; i < passengers.size(); i++) {
            passengerIds[i] = passengers.get(i).getEntityId();
        }
        return passengerIds;
    }

    public Packet<?> initialSpawnPacket(Player target) {
        ClientboundSetEntityDataPacket initialCreatePacket = new ClientboundSetEntityDataPacket(this.customName.getNametagId(), List.of(
            ofData(Display.DATA_BILLBOARD_RENDER_CONSTRAINTS_ID, Display.BillboardConstraints.CENTER.getId())
        ));
        Packet<ClientGamePacketListener> syncData = syncDataPacket(target);

        return new ClientboundBundlePacket(List.of(
            createPacket(), // Create entity
            initialCreatePacket,
            syncData,
            this.getRiderPacket()
        ));
    }

    // int id, UUID uuid, double x, double y, double z, float pitch, float yaw, EntityType<?> entityType, int entityData, Vec3 velocity, double headYaw
    private Packet<ClientGamePacketListener> createPacket() {
        var ridingOffset = this.customName.getPassengerOffset();
        Location location = this.customName.getTargetEntity().getLocation();

        return new ClientboundAddEntityPacket(
            this.customName.getNametagId(),
            UUID.randomUUID(),
            location.x() + ridingOffset.x,
            location.y() + ridingOffset.y, // Put the entity as close as possible to prevent lerping
            location.z() + ridingOffset.z,
            0f,
            0f,
            EntityType.TEXT_DISPLAY,
            0,
            Vec3.ZERO,
            0
        );
    }

    private static <T> SynchedEntityData.DataValue<T> ofData(EntityDataAccessor<T> data, T value) {
        return new SynchedEntityData.DataItem<>(data, value).value();
    }
}
