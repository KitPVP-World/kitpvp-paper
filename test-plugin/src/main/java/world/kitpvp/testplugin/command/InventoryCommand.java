package world.kitpvp.testplugin.command;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import world.kitpvp.testplugin.TestPlugin;
import world.kitpvp.testplugin.inventory.StandardInventoryTest;
import world.kitpvp.testplugin.inventory.VirtualViewTestA;

public class InventoryCommand {
    private static int inventoryTask = -1;

    public static void register(Commands commands) {
        commands.register(
            Commands.literal("inventory")
                .then(Commands.literal("virtual")
                    .executes((context) -> {
                        Entity entity = context.getSource().getExecutor();
                        if (!(entity instanceof Player targetPlayer)) {
                            context.getSource().getSender().sendRichMessage("<red>Executing entity is not a player.");
                            return 0;
                        }

                        VirtualViewTestA virtualView = new VirtualViewTestA(targetPlayer);
                        virtualView.open();

                        context.getSource().getSender().sendRichMessage("Open Virtual Inventory");
                        return Command.SINGLE_SUCCESS;
                    }))
                .then(Commands.literal("standard")
                    .executes((context) -> {
                        Entity entity = context.getSource().getExecutor();
                        if (!(entity instanceof Player targetPlayer)) {
                            context.getSource().getSender().sendRichMessage("<red>Executing entity is not a player.");
                            return 0;
                        }

                        StandardInventoryTest standardInventory = new StandardInventoryTest(targetPlayer, null);
                        standardInventory.open();

                        context.getSource().getSender().sendRichMessage("Open Standard Inventory");
                        return Command.SINGLE_SUCCESS;
                    }))
                .then(Commands.literal("task")
                    .executes((context) -> {
                        if (inventoryTask != -1) {
                            inventoryTask = -1;
                            Bukkit.getScheduler().cancelTask(inventoryTask);
                            context.getSource().getSender().sendRichMessage("Stopped");

                            return Command.SINGLE_SUCCESS;
                        }

                        Entity entity = context.getSource().getExecutor();
                        if (!(entity instanceof Player targetPlayer)) {
                            context.getSource().getSender().sendRichMessage("<red>Executing entity is not a player.");
                            return 0;
                        }

                        TestPlugin testPlugin = TestPlugin.getPlugin(TestPlugin.class);
                        testPlugin.getServer().getScheduler().scheduleSyncRepeatingTask(testPlugin, new Runnable() {
                            int currentSlot = 0;

                            @Override
                            public void run() {
                                ItemStack currentItem = targetPlayer.getInventory().getItem(currentSlot);
                                if (currentItem != null && currentItem.getType() == Material.AMETHYST_BLOCK && currentItem.getAmount() == (currentSlot + 1)) {
                                    targetPlayer.getInventory().setItem(currentSlot, null);
                                }
                                currentSlot = (currentSlot + 1) % 36;

                                targetPlayer.getInventory().setItem(currentSlot, ItemStack.of(Material.AMETHYST_BLOCK, currentSlot + 1));
                            }

                        }, 0, 20);

                        context.getSource().getSender().sendRichMessage("Start Inventory Task");
                        return Command.SINGLE_SUCCESS;
                    }))
                .then(Commands.literal("drop")
                    .executes((context) -> {
                        Entity entity = context.getSource().getExecutor();
                        if (!(entity instanceof Player targetPlayer)) {
                            context.getSource().getSender().sendRichMessage("<red>Executing entity is not a player.");
                            return 0;
                        }

                        targetPlayer.dropItem(true);

                        context.getSource().getSender().sendRichMessage("Drop Item");
                        return Command.SINGLE_SUCCESS;
                    }))
                .build()
        );
    }


}
