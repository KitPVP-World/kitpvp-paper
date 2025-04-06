package world.kitpvp.slime.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
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



    public DisplayPlayer getDisplayPlayer() {
        return displayPlayer;
    }
}
