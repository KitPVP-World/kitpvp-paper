package world.kitpvp.testplugin.command;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.level.chunk.SlimeChunkLevel;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;
import io.papermc.paper.math.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.TriState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.kitpvp.testplugin.arena.ArenaResetHandler;
import world.kitpvp.testplugin.command.exception.DynamicComponentExceptionType;

public class TestCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCommand.class);
    public static final DynamicComponentExceptionType ERROR_INVALID_WORLD = new DynamicComponentExceptionType(
        id -> Component.text("Unknown dimension or not a slime world '")
            .append(id)
            .append(Component.text("'"))
    );

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("test")
                .then(Commands.literal("spawn_all_entities")
                    .executes((ctx) -> {
                        EntityType[] entityTypes = EntityType.values();
                        int index = 0;
                        for (EntityType entityType : entityTypes) {
                            if (!entityType.isSpawnable())
                                continue;

                            Location location = ctx.getSource().getLocation().add(2 * index, 0, 0);

                            ctx.getSource().getLocation().getWorld().spawnEntity(location, entityType, CreatureSpawnEvent.SpawnReason.COMMAND, (entity) -> {
                                entity.setGravity(false);
                                entity.setCustomNameVisible(true);
                                entity.setPersistent(true);
                                entity.customName(Component.text(entityType.name(), TextColor.color(0xffba00)));
                                if (entity instanceof Mob mob) {
                                    mob.setDespawnInPeacefulOverride(TriState.FALSE);
                                    mob.setAI(false);


                                    mob.getEquipment().setHelmet(ItemType.CHAINMAIL_HELMET.createItemStack((meta) -> {
                                        meta.setUnbreakable(true);
                                    }));
                                }

                            });
                            location.clone().subtract(0, 1, 0).getBlock().setType(Material.STONE);

                            Block block = location.clone().subtract(0, 1, 1).getBlock();
                            block.setType(Material.OAK_WALL_SIGN);

                            if (block.getState() instanceof Sign sign && block.getBlockData() instanceof WallSign wallSign) {
                                wallSign.setFacing(BlockFace.SOUTH);
                                sign.getSide(Side.FRONT).setGlowingText(true);
                                sign.getSide(Side.FRONT).line(0, Component.text(entityType.name(), TextColor.color(0xffba00)));

                                block.setBlockData(wallSign);
                                sign.update();
                            }

                            index++;
                        }


                        return Command.SINGLE_SUCCESS;
                    }))
                .then(Commands.literal("light_level")
                    .executes((context) -> {
                        var location = context.getSource().getLocation();
                        var lightLevel = location.getBlock().getLightLevel();

                        context.getSource().getSender().sendRichMessage("The light level is " + lightLevel);

                        return lightLevel;
                    }))
                .then(Commands.literal("reset_world")
                    .executes((context) -> {
                        String templateWorldName = "kungfu";
                        SlimeWorld templateWorld = AdvancedSlimePaperAPI.instance().getLoadedWorld(templateWorldName);

                        if (templateWorld == null) {
                            throw ERROR_INVALID_WORLD.create(Component.text(templateWorldName));
                        }

                        ChunkPos min = new ChunkPos(9, 5);
                        ChunkPos max = new ChunkPos(16, 12);

                        long startTime = System.currentTimeMillis();

                        ArenaResetHandler.resetChunks(templateWorld, context.getSource().getLocation().getWorld(), min, max).thenRun(() ->
                            context.getSource().getSender().sendRichMessage("<green>Finished resetting chunks in " + (System.currentTimeMillis() - startTime) + "ms!")
                        ).exceptionally((throwable) -> {
                            LOGGER.error("Failed to reset chunks", throwable);
                            throw new RuntimeException("Failed to reset chunks", throwable);
                        });

                        int width = max.x - min.x + 1;
                        int length = max.z - min.z + 1;

                        return width * length;
                    }))
                .then(Commands.literal("block")
                    .then(Commands.argument("pos", ArgumentTypes.blockPosition())
                        .executes((context) -> {
                            final BlockPositionResolver blockPositionResolver = context.getArgument("pos", BlockPositionResolver.class);
                            final BlockPosition blockPosition = blockPositionResolver.resolve(context.getSource());

                            Block block = context.getSource().getLocation().getWorld()
                                .getBlockAt(blockPosition.blockX(), blockPosition.blockY(), blockPosition.blockZ());

                            context.getSource().getSender().sendRichMessage("Block at " + blockPosition + " is <lang:" + block.getType().getBlockTranslationKey() + ">");

                            return Command.SINGLE_SUCCESS;
                        })))
                .then(Commands.literal("reset_chunk")
                    .executes((context) -> {
                        SlimeWorld templateWorld = AdvancedSlimePaperAPI.instance().getLoadedWorld("kungfu");
                        Location location = context.getSource().getLocation();
                        Chunk bukkitChunk = location.getChunk();
                        int x = bukkitChunk.getX(),
                            z = bukkitChunk.getZ();
                        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();

                        long startSerializationTime = System.currentTimeMillis();
                        SlimeChunkLevel chunk = ArenaResetHandler.loadTemplateChunk(templateWorld, level, x, z);
                        context.getSource().getSender().sendMessage("Loaded chunk data in " + (System.currentTimeMillis() - startSerializationTime) + "ms");

                        long startTime = System.currentTimeMillis();
                        ArenaResetHandler.resetChunk(level, chunk);

                        context.getSource().getSender().sendMessage("Cleared chunk data in " + (System.currentTimeMillis() - startTime) + "ms");

                        return Command.SINGLE_SUCCESS;
                    })
                )
                .build()
        );
    }
}
