package world.kitpvp.slime.server;

import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class KitPvPGameRulesImpl {

    public static final GameRule<Boolean> PREDICTABLE_ENCHANTMENTS = GameRules.registerBoolean("kitpvp:predictable_enchantments", GameRuleCategory.MISC, true);
    public static final GameRule<Boolean> PREDICTABLE_PROJECTILES = GameRules.registerBoolean("kitpvp:predictable_projectiles", GameRuleCategory.MISC, true);

    public static void register() {

    }

}
