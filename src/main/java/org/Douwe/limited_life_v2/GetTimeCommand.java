package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
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

    public void register2(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                        .then(literal("getTimeOf")
                                        .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                                        .executes(ctx -> {

                                                            Collection<PlayerConfigEntry> profile =
                                                                    GameProfileArgumentType.getProfileArgument(ctx, "player");
                                                            for(PlayerConfigEntry p : profile){
                                                                UUID player = p.id();
                                                                String name = p.name();
                                                                String time = Limited_life_v2.secToTime((int) Limited_life_v2.getPlayerTimeLeft(player));
                                                                ctx.getSource().getPlayer().sendMessage(Text.literal(name +  "'s time: "+ time));
                                                            }
                                                            return 1;
                                                        })
                                        )
                        )
        );
    }


}
