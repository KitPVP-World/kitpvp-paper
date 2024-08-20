package world.kitpvp.slime.test.commands;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.ComponentLike;

import java.util.function.Function;

public class DynamicComponentExceptionType implements CommandExceptionType {
    private final Function<ComponentLike, ComponentLike> function;

    public DynamicComponentExceptionType(final Function<ComponentLike, ComponentLike> function) {
        this.function = function;
    }

    public CommandSyntaxException create(final ComponentLike argument) {
        return new CommandSyntaxException(this, MessageComponentSerializer.message().serialize(this.function.apply(argument).asComponent()));
    }

    public CommandSyntaxException createWithContext(final ComponentLike argument, final ImmutableStringReader reader) {
        return new CommandSyntaxException(this, MessageComponentSerializer.message().serialize(this.function.apply(argument).asComponent()), reader.getString(), reader.getCursor());
    }
}
