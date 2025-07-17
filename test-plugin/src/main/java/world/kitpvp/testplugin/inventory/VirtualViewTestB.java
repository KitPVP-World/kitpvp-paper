package world.kitpvp.testplugin.inventory;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VirtualViewTestB extends VirtualViewTest {


    public VirtualViewTestB(Player player, VirtualViewTestA testA) {
        super(player,  Component.text("Virtual Test B").color(NamedTextColor.GREEN));

        button(topSlot(8), ItemStack.of(Material.BEDROCK), () ->
            this.player().playSound(Sound.sound(Key.key("block.bedrock.break"), Sound.Source.UI, 1.0f, 1.0f))
        );
        button(topSlot(17), ItemStack.of(Material.RED_GLAZED_TERRACOTTA), () -> {
            this.player().playSound(Sound.sound(Key.key("block.note_block.didgeridoo"), Sound.Source.UI, 1.0f, 1.0f));
            testA.open();
        });
        button(bottomSlot(10), ItemStack.of(Material.GREEN_BANNER, 2), () -> {
            this.player().playSound(Sound.sound(Key.key("entity.breeze.death"), Sound.Source.UI, 1.0f, 1.0f));

            VirtualViewTest virtualViewTest = new VirtualViewTestHopper(this.player(), testA);
            virtualViewTest.open();
        });
    }
}
