package world.kitpvp.testplugin.command.exception;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.ComponentLike;

public class ComponentExceptionType implements CommandExceptionType {
    private final ComponentLike component;

    public ComponentExceptionType(ComponentLike component) {
        this.component = component;
    }

    public CommandSyntaxException create() {
        return new CommandSyntaxException(this, MessageComponentSerializer.message().serialize(this.component.asComponent()));
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader) {
        return new CommandSyntaxException(this, MessageComponentSerializer.message().serialize(this.component.asComponent()), reader.getString(), reader.getCursor());
    }
}
