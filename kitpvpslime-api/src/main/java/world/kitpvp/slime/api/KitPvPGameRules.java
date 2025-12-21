package world.kitpvp.slime.api;

import net.kyori.adventure.key.KeyPattern;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class KitPvPGameRules {
    public static final GameRule<Boolean> PREDICTABLE_ENCHANTMENTS = getRule("predictable_enchantments");
    public static final GameRule<Boolean> PREDICTABLE_PROJECTILES = getRule("predictable_projectiles");

    private static <T> GameRule<T> getRule(@KeyPattern.Value String key) {
        return (GameRule<T>) Registry.GAME_RULE.getOrThrow(new NamespacedKey("kitpvp", key));
    }
}
