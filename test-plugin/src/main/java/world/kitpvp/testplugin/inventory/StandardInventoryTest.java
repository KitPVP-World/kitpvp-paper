package world.kitpvp.testplugin.inventory;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

public class StandardInventoryTest extends StandardInventory {

    private final Runnable[] actions;

    public StandardInventoryTest(Player player, @Nullable VirtualViewTest previousView) {
        super(player, Component.text("Standard Inventory").color(NamedTextColor.YELLOW));

        this.actions = new Runnable[this.getInventory().getSize()];

        button(1, ItemStack.of(Material.GOLD_BLOCK), () ->
            this.player().playSound(Sound.sound(Key.key("block.note_block.imitate.piglin"), Sound.Source.UI, 1.0f, 1.0f))
        );
        button(2, ItemStack.of(Material.GREEN_GLAZED_TERRACOTTA), () -> {
            if (previousView != null) {
                this.player().playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.UI, 1.0f, 1.0f));
                previousView.open();
            } else {
                this.player().playSound(Sound.sound(Key.key("entity.enderman.teleport"), Sound.Source.UI, 1.0f, 0.5f));
            }
        });

        button(0, ItemStack.of(Material.YELLOW_BANNER), () -> {
            this.player().playSound(Sound.sound(Key.key("entity.bee.hurt"), Sound.Source.UI, 1.0f, 0.5f));
            VirtualViewTestA virtualView = new VirtualViewTestA(this.player());
            virtualView.open();
        });
    }

    public void open() {
        this.player().openInventory(this.getInventory());
    }

    public void button(int slot, ItemStack item, Runnable action) {
        this.getInventory().setItem(slot, item);
        this.actions[slot] = action;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getRawSlot() < 0 || event.getRawSlot() >= this.actions.length) {
            return;
        }
        Runnable action = this.actions[event.getSlot()];
        if (action != null) {
            action.run();
        }
    }
}
