package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.playerList;

public class SetTimeCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                .then(literal("setTimeOf")
                        .requires(Permissions.require("limited_life_v2.command", 4))
                        .then(argument("player", EntityArgument.player())
                                .then(Commands.argument("time", FloatArgumentType.floatArg())
                                        .executes(ctx -> {
                                            ServerPlayer player =
                                                    EntityArgument.getPlayer(ctx, "player");
                                            Float time =
                                                    FloatArgumentType.getFloat(ctx, "time");
                                            UUID id = player.getUUID();
                                            playerList.replace(id, time);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
