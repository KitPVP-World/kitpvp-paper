package world.kitpvp.slime.entity;


import com.mojang.authlib.GameProfile;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisplayPlayer extends PathfinderMob {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayPlayer.class);
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();
    private ServerPlayer defaultServerPlayer;

    private boolean aiDisabled;
    private boolean aggressive;
    private String name = "Bot";

    public DisplayPlayer(EntityType<DisplayPlayer> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.setCanPickUpLoot(true);

        var defaultServerPlayer = getDefaultServerPlayer();
        defaultServerPlayer.setPos(this.position());
        defaultServerPlayer.xo = this.xo;
        defaultServerPlayer.yo = this.yo;
        defaultServerPlayer.zo = this.zo;
        defaultServerPlayer.connection = new ServerGamePacketListenerImpl(
            this.serverLevel().getServer(),
            new net.minecraft.network.Connection(PacketFlow.SERVERBOUND),
            defaultServerPlayer,
            CommonListenerCookie.createInitial(defaultServerPlayer.getGameProfile(), false)
        );
        defaultServerPlayer.setAttributes(getAttributes());
        this.entityData.set(Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) 127);
    }

    public DisplayPlayer(Level level, double x, double y, double z) {
        this(EntityType.DISPLAY_PLAYER, level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public @NotNull ClientboundPlayerInfoUpdatePacket.Entry getInfoUpdateEntry(@NotNull ServerPlayer forPlayer) {
        var gameProfile = new GameProfile(
            this.getUUID(),
            this.name
        );
        gameProfile.getProperties().putAll(forPlayer.getGameProfile().getProperties());

        return new ClientboundPlayerInfoUpdatePacket.Entry(
            gameProfile.getId(),
            gameProfile,
            true,
            0,
            GameType.SURVIVAL,
            forPlayer.getDisplayName(),
            true,
            0,
            null
        );
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entityTrackerEntry) {
        return new ClientboundAddEntityPacket( // Cloned from other constructor
            this.getId(),
            this.getUUID(),
            this.trackingPosition().x(),
            this.trackingPosition().y(),
            this.trackingPosition().z(),
            entityTrackerEntry.getLastSentXRot(),
            entityTrackerEntry.getLastSentYRot(),
            EntityType.PLAYER, // only parameter changed
            0,
            entityTrackerEntry.getLastSentMovement(),
            entityTrackerEntry.getLastSentYHeadRot()
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        this.getDefaultServerPlayer().defineSynchedData(builder);
    }

    public ServerPlayer getDefaultServerPlayer() {
        if (this.defaultServerPlayer == null) {
            this.defaultServerPlayer = new DisplayServerPlayer(this);
        }

        return this.defaultServerPlayer;
    }

    public ServerLevel serverLevel() {
        return (ServerLevel) this.level();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.MOVEMENT_SPEED, 0.10000000149011612D).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK).add(Attributes.BLOCK_INTERACTION_RANGE, 4.5D).add(Attributes.ENTITY_INTERACTION_RANGE, 3.0D).add(Attributes.BLOCK_BREAK_SPEED).add(Attributes.SUBMERGED_MINING_SPEED).add(Attributes.SNEAKING_SPEED).add(Attributes.MINING_EFFICIENCY).add(Attributes.SWEEPING_DAMAGE_RATIO);
    }

    @Override
    public void tick() {
        this.noPhysics = this.isSpectator();
        if (this.isSpectator()) {
            this.setOnGround(false);
        }

        super.tick();
        var defaultServerPlayer = getDefaultServerPlayer();
        for (var slot : EQUIPMENT_SLOTS)
            defaultServerPlayer.setItemSlot(slot, this.getItemBySlot(slot));
        defaultServerPlayer.setPos(this.trackingPosition());
        this.setHealth(defaultServerPlayer.getHealth());
        defaultServerPlayer.tick();
        this.setPose(defaultServerPlayer.getPose());
    }


    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        input.getString("playerName").ifPresent(this::name);
        var defaultPlayer = this.getDefaultServerPlayer();

        // cloned and modified from Player
        defaultPlayer.getFoodData().readAdditionalSaveData(input);
        input.read("abilities", Abilities.Packed.CODEC).ifPresent(defaultPlayer.getAbilities()::apply);
        defaultPlayer.setLastDeathLocation(input.read("LastDeathLocation", GlobalPos.CODEC));
//        this.setCustomName(Component.literal(this.name));
    }


    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        var defaultPlayer = this.getDefaultServerPlayer();

        // cloned and modified from Player
        defaultPlayer.getFoodData().addAdditionalSaveData(output);
        output.store("abilities", Abilities.Packed.CODEC, defaultPlayer.getAbilities().pack());
        defaultPlayer.getLastDeathLocation().ifPresent(globalPos -> output.store("LastDeathLocation", GlobalPos.CODEC, globalPos));
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        if (super.hurtServer(level, damageSource, amount)) {
            if (this.level().getLevelData().getDifficulty() == Difficulty.PEACEFUL) {
                LOGGER.warn("AI of Display Player is not able to function correctly because the server difficulty is set to peaceful.");
            }

            return this.getDefaultServerPlayer().hurtServer(level, damageSource, amount);
        } else
            return false;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected @NotNull HoverEvent createHoverEvent() {
        return new HoverEvent.ShowEntity(new HoverEvent.EntityTooltipInfo(EntityType.PLAYER, this.getUUID(), this.getName()));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return source.type().effects().sound();
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    public @NotNull SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    public @NotNull SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    public @NotNull SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    public LivingEntity.@NotNull Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
    }


    /*
    Replacing the synched entity data with local variables (players get kicked if one sent them as synched ones)
     */

    @Override
    public void setNoAi(boolean aiDisabled) {
        this.aiDisabled = aiDisabled;
    }

    @Override
    public void setLeftHanded(boolean leftHanded) {
        this.entityData.set(Player.DATA_PLAYER_MAIN_HAND, (byte) (leftHanded ? 0 : 1));
    }

    @Override
    public boolean isLeftHanded() {
        return this.entityData.get(Player.DATA_PLAYER_MAIN_HAND) == 0;
    }

    @Override
    public void setAggressive(boolean attacking) {
        this.aggressive = attacking;
    }

    @Override
    public boolean isNoAi() {
        return this.aiDisabled;
    }

    @Override
    public boolean isAggressive() {
        return this.aggressive;
    }

    public String name() {
        return this.name;
    }

    public void name(String name) {
        this.name = name;
    }
}
