package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GetTimeCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                .then(literal("getTimeOf")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(ctx -> {
                                    ServerPlayerEntity player =
                                            EntityArgumentType.getPlayer(ctx, "player");
                                    UUID id = player.getUuid();
                                    String time = Limited_life_v2.secToTime((int) Limited_life_v2.getPlayerTimeLeft(id));
                                    ctx.getSource().getPlayer().sendMessage(Text.literal(player.getStringifiedName()+  "'s time: "+ time));
                                    return 1;
                                })
                        )
                )
        );
    }


}
