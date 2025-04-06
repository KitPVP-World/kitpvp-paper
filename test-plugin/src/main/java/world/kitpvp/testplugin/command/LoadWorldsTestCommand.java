package world.kitpvp.testplugin.command;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.kitpvp.testplugin.command.exception.DynamicComponentExceptionType;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class LoadWorldsTestCommand {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadWorldsTestCommand.class);
    public static final DynamicComponentExceptionType ERROR_INVALID_VALUE = new DynamicComponentExceptionType(
        id -> Component.translatable("argument.dimension.invalid", id)
    );

    public static void register(Commands commands, Path dataDirectory) {
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

                        load(context.getSource().getSender(), world, dataDirectory);
                        return Command.SINGLE_SUCCESS;
                    })).build());
    }

    private static void load(CommandSender source, SlimeWorld masterWorld, Path dataDirectory) {
        int worldsToLoad = 10000;
        long started = System.currentTimeMillis();
        long firstWorldLoad = 0;
        long max = 0;
        long maxNotFirst = 0;
        long[] testResults = new long[worldsToLoad];
//        masterWorld.getPropertyMap().setValue(DuelSlimeProperties.USE_DEFAULT_CONFIG, true);

        for(int i = 0; i < worldsToLoad; i++) {
            long worldStartTime = System.currentTimeMillis();
            SlimeWorld copySpam = masterWorld.clone(masterWorld.getName() + "-" + UUID.randomUUID());
            LOGGER.info("Cloning world {} to {}", masterWorld.getName(), copySpam.getName());

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
            testResults[i] = worldTime;

            if(worldTime > max)
                max = worldTime;

            if(i == 0)
                firstWorldLoad = worldTime;
            else if(worldTime > maxNotFirst)
                maxNotFirst = worldTime;

        }

        long totalTime = (System.currentTimeMillis() - started);
        source.sendMessage(Component.text("Loaded/Unloaded " + worldsToLoad + " worlds in " + totalTime + "ms by masterWorld " + masterWorld));
        LOGGER.info("-- Analytics of test --");
        LOGGER.info("First world took: {}", firstWorldLoad);
        LOGGER.info("Total time: {}", totalTime);
        LOGGER.info("Total time (excluding first): {}", totalTime - firstWorldLoad);
        LOGGER.info("AVG: {}", totalTime / worldsToLoad);
        LOGGER.info("AVG (excluding first): {}", totalTime / worldsToLoad);
        LOGGER.info("Max: {}", max);
        LOGGER.info("Max (excluding first): {}", maxNotFirst);

        Path resultsPath = dataDirectory.resolve("result-" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + ".csv");
        LOGGER.info("Writing test results to {}", resultsPath);

        try {
            Files.createDirectories(resultsPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try(FileWriter writer = new FileWriter(resultsPath.toFile())) {
            writer.write("Measurement (ms)" + System.lineSeparator());
            for (long testResult : testResults) {
                writer.write(testResult + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Finished saving test results");
    }
}
