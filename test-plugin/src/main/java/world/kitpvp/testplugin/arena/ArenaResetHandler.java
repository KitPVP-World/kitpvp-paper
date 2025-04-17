package world.kitpvp.testplugin.arena;

import ca.spottedleaf.moonrise.patches.chunk_system.level.entity.EntityLookup;
import ca.spottedleaf.moonrise.patches.chunk_system.scheduling.NewChunkHolder;
import ca.spottedleaf.moonrise.patches.starlight.light.StarLightEngine;
import ca.spottedleaf.moonrise.patches.starlight.light.StarLightInterface;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.level.SlimeChunkLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class ArenaResetHandler {
    public static CompletableFuture<Void> resetChunks(SlimeWorld templateWorld, World world, ChunkPos min, ChunkPos max) {
        ServerLevel level = ((CraftWorld) world).getHandle();

        Predicate<BlockPos> inResettingRegion = (pos) -> {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;

            return (chunkX >= min.x && chunkX <= max.x)
                || (chunkZ >= min.z && chunkZ <= max.z);
        };

        return CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();

            int width = max.x - min.x + 1;
            int length = max.z - min.z + 1;
            SlimeChunkLevel[] chunks = new SlimeChunkLevel[width * length];

            int index = 0;
            for(int x = min.x; x <= max.x; x++) {
                for(int z = min.z; z <= max.z; z++) {
                    chunks[index] = loadTemplateChunk(templateWorld, level, x, z);
                    index++;
                }
            }

            System.out.println("Async serialization step took " + (System.currentTimeMillis() - startTime) + "ms");

            return chunks;
        }).thenAcceptAsync(chunks -> {
            long startTime = System.currentTimeMillis();

            // remove cache
            level.capturedBlockStates.keySet().removeIf(inResettingRegion);
            level.capturedTileEntities.keySet().removeIf(inResettingRegion);

            // reset chunks
            for(SlimeChunkLevel chunk : chunks) {
                resetChunkKeepCache(level, chunk);
            }

            System.out.println("Reset chunks on main thread in " + (System.currentTimeMillis() - startTime) + "ms");
        }, MinecraftServer.getServer());
    }

    public static SlimeChunkLevel loadTemplateChunk(SlimeWorld templateWorld, ServerLevel level, int x, int z) {
        return (SlimeChunkLevel) level.slimeInstance.createChunk(x, z, templateWorld.getChunk(x, z));
    }

    public static void resetChunk(ServerLevel level, SlimeChunkLevel newLevelChunk) {
        int x = newLevelChunk.locX, z = newLevelChunk.locZ;
        level.capturedBlockStates.keySet().removeIf((pos) -> (pos.getX() >> 4) == x && pos.getZ() >> 4 == z);
        level.capturedTileEntities.keySet().removeIf((pos) -> (pos.getX() >> 4) == x && pos.getZ() >> 4 == z);

        resetChunkKeepCache(level, newLevelChunk);
    }

    private static void resetChunkKeepCache(ServerLevel level, SlimeChunkLevel newLevelChunk) {
        int x = newLevelChunk.locX, z = newLevelChunk.locZ;
        EntityLookup entityLookup = level.moonrise$getEntityLookup();

        // unload old chunk
        LevelChunk oldLevelChunk = level.getChunk(x, z);
        level.unload(oldLevelChunk);
        level.moonrise$removeChunkForPlayerTicking(oldLevelChunk);
        for (Entity entity : entityLookup.getChunk(x, z).getAllEntities()) {
            if (!(entity instanceof ServerPlayer))
                entity.remove(Entity.RemovalReason.DISCARDED);
        }

        // load new chunk
        level.chunkSource.moonrise$setFullChunk(x, z, newLevelChunk);
        level.slimeInstance.promoteInChunkStorage(newLevelChunk);

        // see: ca.spottedleaf.moonrise.patches.chunk_system.scheduling.task.ChunkFullTask#run
        NewChunkHolder chunkHolder = level.moonrise$getChunkTaskScheduler().chunkHolderManager.getChunkHolder(x, z);
        newLevelChunk.moonrise$setChunkAndHolder(new ServerChunkCache.ChunkAndHolder(newLevelChunk, chunkHolder.vanillaChunkHolder));

        newLevelChunk.runPostLoad();
        level.moonrise$getChunkTaskScheduler().chunkHolderManager.getOrCreateEntityChunk(x, z, false);
        newLevelChunk.setLoaded(true);
        newLevelChunk.registerAllBlockEntitiesAfterLevelLoad();
        newLevelChunk.registerTickContainerInLevel(level);
        newLevelChunk.needsDecoration = false;
        newLevelChunk.loadCallback();

        chunkHolder.onChunkGenComplete(newLevelChunk, ChunkStatus.FULL, List.of(), List.of());

        // complete and notify players
        level.moonrise$markChunkForPlayerTicking(newLevelChunk);

        level.moonrise$getChunkTaskScheduler().scheduleChunkTask(x, z, () -> {
            lightChunk(level, newLevelChunk);

            List<ServerPlayer> playersToBroadcastChunk = chunkHolder.vanillaChunkHolder.moonrise$getPlayers(false);
            for(var player : playersToBroadcastChunk) {
                player.connection.chunkSender.markChunkPendingToSend(newLevelChunk);
            }
        });
    }

    public static void lightChunk(ServerLevel level, ChunkAccess chunk) {
        StarLightInterface lightInterface = level.chunkSource.getLightEngine().starlight$getLightEngine();

        final Boolean[] emptySections = StarLightEngine.getEmptySectionsForChunk(chunk);
        if(!chunk.isLightCorrect()) {
            lightInterface.lightChunk(chunk, emptySections);
            chunk.setLightCorrect(true);
        }
    }

}
