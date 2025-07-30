package world.kitpvp.slime.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jspecify.annotations.NullMarked;

import javax.annotation.Nullable;

@NullMarked
public final class DisplayServerPlayer extends ServerPlayer {
    private final DisplayPlayer displayPlayer;

    DisplayServerPlayer(DisplayPlayer displayPlayer) {
        super(((ServerLevel) displayPlayer.level()).getServer(), (ServerLevel) displayPlayer.level(), new GameProfile(displayPlayer.getUUID(), "defaultbot"), ClientInformation.createDefault());
        this.setId(displayPlayer.getId());
        this.displayPlayer = displayPlayer;
    }

    @Override
    public boolean addEffect(MobEffectInstance effectInstance, @Nullable Entity entity, EntityPotionEffectEvent.Cause cause, boolean fireEvent) {
        return this.displayPlayer.addEffect(effectInstance, entity, cause, fireEvent);
    }

    @Override
    public TeleportTransition findRespawnPositionAndUseSpawnBlock(boolean useCharge, TeleportTransition.PostTeleportTransition postTeleportTransition, @org.jetbrains.annotations.Nullable PlayerRespawnEvent.RespawnReason respawnReason) {
        TeleportTransition teleportTransition = new TeleportTransition(this.level(), this.position(), Vec3.ZERO, this.getYRot(), this.getXRot(), (e) -> {});
        if(respawnReason == null) {
            return teleportTransition;
        }

        org.bukkit.entity.Player respawnPlayer = this.getBukkitEntity();
        org.bukkit.Location location = org.bukkit.craftbukkit.util.CraftLocation.toBukkit(
            teleportTransition.position(),
            teleportTransition.newLevel().getWorld(),
            teleportTransition.yRot(),
            teleportTransition.xRot()
        );

        org.bukkit.event.player.PlayerRespawnEvent respawnEvent = new org.bukkit.event.player.PlayerRespawnEvent(
            respawnPlayer,
            location,
            false,
            false,
            teleportTransition.missingRespawnBlock(),
            respawnReason
        );
        // Paper end - respawn flags
        this.level().getCraftServer().getPluginManager().callEvent(respawnEvent);

        location = respawnEvent.getRespawnLocation();

        return new TeleportTransition(
            ((org.bukkit.craftbukkit.CraftWorld) location.getWorld()).getHandle(),
            org.bukkit.craftbukkit.util.CraftLocation.toVec3(location),
            teleportTransition.deltaMovement(),
            location.getYaw(),
            location.getPitch(),
            teleportTransition.missingRespawnBlock(),
            teleportTransition.asPassenger(),
            teleportTransition.relatives(),
            teleportTransition.postTeleportTransition(),
            teleportTransition.cause()
        );
    }

    public DisplayPlayer getDisplayPlayer() {
        return this.displayPlayer;
    }
}
