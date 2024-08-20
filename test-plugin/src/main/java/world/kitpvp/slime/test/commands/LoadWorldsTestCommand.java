package world.kitpvp.slime.test.commands;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import world.kitpvp.aswm.api.DuelSlimeProperties;

import java.util.UUID;

public class LoadWorldsTestCommand {
    public static final DynamicComponentExceptionType ERROR_INVALID_VALUE = new DynamicComponentExceptionType(
        id -> Component.translatable("argument.dimension.invalid", id)
    );

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("loadworlds")
                .then(Commands.argument("world", StringArgumentType.word())
                    .suggests((commandContext, suggestionsBuilder) -> {
                        for(var slimeWorld: AdvancedSlimePaperAPI.instance().getLoadedWorlds())
                            suggestionsBuilder.suggest(slimeWorld.getName());

                        return suggestionsBuilder.buildFuture();
                    })
                    .executes((context) -> {
                        var worldName = context.getArgument("world", String.class);
                        var world = AdvancedSlimePaperAPI.instance().getLoadedWorld(worldName);
                        if(world == null) {
                            throw ERROR_INVALID_VALUE.create(Component.text(worldName));
                        }

                        load(context.getSource().getSender(), world);
                        return Command.SINGLE_SUCCESS;
                    })).build());
    }

    private static void load(CommandSender source, SlimeWorld masterWorld) {
        int worldsToLoad = 10000;
        long started = System.currentTimeMillis();
        long firstWorldLoad = 0;
        long max = 0;
        long maxNotFirst = 0;
        masterWorld.getPropertyMap().setValue(DuelSlimeProperties.USE_DEFAULT_CONFIG, true);

        for(int i = 0; i < worldsToLoad; i++) {
            long worldStartTime = System.currentTimeMillis();
            SlimeWorld copySpam = masterWorld.clone(masterWorld.getName() + "-" + UUID.randomUUID());
            Bukkit.getLogger().info("Cloning world " + masterWorld.getName() + " to " + copySpam.getName());

            World world;
            try {
                AdvancedSlimePaperAPI.instance().loadWorld(copySpam, false);
            } catch (Exception e) {
                Bukkit.getServer().getLogger().severe(e.getMessage());
            }

            world = Bukkit.getWorld(copySpam.getName());
            if(world == null) {
                Bukkit.getServer().getLogger().severe("Failed to load world " + copySpam.getName());
                continue;
            }

            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            world.setGameRule(GameRule.ENDER_PEARLS_VANISH_ON_DEATH, true);
            world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

            Bukkit.unloadWorld(world, false);
            var worldTime = (System.currentTimeMillis() - worldStartTime);
            if(worldTime > max)
                max = worldTime;

            if(i == 0)
                firstWorldLoad = worldTime;
            else if(worldTime > maxNotFirst)
                maxNotFirst = worldTime;

        }

        long totalTime = (System.currentTimeMillis() - started);
        source.sendMessage(Component.text("Loaded/Unloaded " + worldsToLoad + " worlds in " + totalTime + "ms by masterWorld " + masterWorld));
        Bukkit.getLogger().info("-- Analytics of test --");
        Bukkit.getLogger().info("First world took: " + firstWorldLoad);
        Bukkit.getLogger().info("Total time: " + totalTime);
        Bukkit.getLogger().info("Total time (excluding first): " + (totalTime - firstWorldLoad));
        Bukkit.getLogger().info("AVG: " + (totalTime / worldsToLoad));
        Bukkit.getLogger().info("AVG (excluding first): " + (totalTime / worldsToLoad));
        Bukkit.getLogger().info("Max: " + (max));
        Bukkit.getLogger().info("Max (excluding first): " + (maxNotFirst));
    }
}
