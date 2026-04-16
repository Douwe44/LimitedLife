package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class MeBoogeyCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                .then(literal("Am_I_The_Boogey?")
                                .executes(ctx -> {
                                    UUID id = ctx.getSource().getPlayer().getUUID();
                                    if (BoogeymanCommand.boogeyList.contains(id)) {
                                        ctx.getSource().getPlayer().sendSystemMessage(Component.literal("you are the Boogeyman"));
                                    } else {
                                        ctx.getSource().getPlayer().sendSystemMessage(Component.literal("you are NOT the Boogeyman"));

                                    }
                                    return 1;
                                })
                        )
        );
    }
}
