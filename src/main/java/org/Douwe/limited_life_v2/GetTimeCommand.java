package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GetTimeCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                .then(literal("getTimeOf")
                        .then(argument("player", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer player =
                                            EntityArgument.getPlayer(ctx, "player");
                                    UUID id = player.getUUID();
                                    String time = Limited_life_v2.secToTime((int) Limited_life_v2.getPlayerTimeLeft(id));
                                    ctx.getSource().getPlayer().sendSystemMessage(Component.literal(player.getPlainTextName()+  "'s time: "+ time));
                                    return 1;
                                })
                        )
                )
        );
    }

    public void register2(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                        .then(literal("getTimeOf")
                                        .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                                        .executes(ctx -> {

                                                            Collection<NameAndId> profile =
                                                                    GameProfileArgument.getGameProfiles(ctx, "player");
                                                            for(NameAndId p : profile){
                                                                UUID player = p.id();
                                                                String name = p.name();
                                                                String time = Limited_life_v2.secToTime((int) Limited_life_v2.getPlayerTimeLeft(player));
                                                                ctx.getSource().getPlayer().sendSystemMessage(Component.literal(name +  "'s time: "+ time));
                                                            }
                                                            return 1;
                                                        })
                                        )
                        )
        );
    }


}
