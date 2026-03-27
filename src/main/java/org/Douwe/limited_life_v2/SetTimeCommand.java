package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.playerList;

public class SetTimeCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                .then(literal("setTimeOf")
                        .requires(Permissions.require("limited_life_v2.command", 4))
                        .then(argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("time", FloatArgumentType.floatArg())
                                        .executes(ctx -> {
                                            ServerPlayerEntity player =
                                                    EntityArgumentType.getPlayer(ctx, "player");
                                            Float time =
                                                    FloatArgumentType.getFloat(ctx, "time");
                                            UUID id = player.getUuid();
                                            playerList.replace(id, time);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
