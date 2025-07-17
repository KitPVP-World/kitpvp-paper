package world.kitpvp.testplugin.inventory;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class VirtualViewTestA extends VirtualViewTest {
    public VirtualViewTestA(Player player) {
        super(player, Component.text("Virtual Test A").color(NamedTextColor.BLUE));

        button(topSlot(0), ItemStack.of(Material.AMETHYST_BLOCK), () ->
            this.player().playSound(Sound.sound(Key.key("block.amethyst_block.break"), Sound.Source.UI, 1.0f, 1.0f))
        );
        button(topSlot(9), ItemStack.of(Material.PURPLE_GLAZED_TERRACOTTA), () -> {
            this.player().playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.UI, 1.0f, 1.0f));

            VirtualViewTestB virtualViewTestB = new VirtualViewTestB(this.player(), this);
            virtualViewTestB.open();
        });

        for (int i = 0; i < virtualView().getBottomInventory().getSize(); i++) {
            virtualView().getBottomInventory().setItem(i, ItemStack.of(Material.LIGHT, i + 1));
        }

        button(bottomSlot(10), ItemStack.of(Material.BLUE_BANNER, 2), () -> {
            this.player().playSound(Sound.sound(Key.key("entity.breeze.death"), Sound.Source.UI, 1.0f, 1.0f));
            StandardInventoryTest standardInventory = new StandardInventoryTest(this.player(), null);
            standardInventory.open();
        });

        button(bottomSlot(3), ItemStack.of(Material.LIGHT_BLUE_BANNER, 10), () ->
            this.player().playSound(Sound.sound(Key.key("entity.breeze.hurt"), Sound.Source.UI, 1.0f, 1.0f)));

        virtualView().getBottomInventory().setItemInOffHand(ItemStack.of(Material.RED_BANNER));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        boolean wasCancelled = event.isCancelled();
        super.handleClick(event);
        if (event.getRawSlot() == 30 || !event.getCursor().isEmpty()) {
            event.setCancelled(wasCancelled);
        }
    }
}
