package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
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
                                        .then(Commands.argument("player", StringArgumentType.string()).suggests(new SuggestionProvider<CommandSourceStack>() {
                                                            @Override
                                                            public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
                                                                CommandSourceStack source = commandContext.getSource();
                                                                Collection<String> names = Limited_life_v2.playerNames.keySet();
                                                                for(String playerName : names) {
                                                                    suggestionsBuilder.suggest(playerName);
                                                                }
                                                                return suggestionsBuilder.buildFuture();
                                                            }
                                                        })
                                                        .executes(ctx -> {

                                                            String name  =
                                                                    StringArgumentType.getString(ctx, "player");

                                                            UUID player = Limited_life_v2.playerNames.get(name);

                                                            String time = Limited_life_v2.secToTime((int) Limited_life_v2.getPlayerTimeLeft(player));
                                                            ctx.getSource().getPlayer().sendSystemMessage(Component.literal(name +  "'s time: "+ time));

                                                            return 1;
                                                        })
                                        )
                                        .then(literal("allPlayers").executes(ctx -> {
                                            for(String playername : Limited_life_v2.playerNames.keySet()){
                                                UUID player = Limited_life_v2.playerNames.get(playername);
                                                String time = Limited_life_v2.secToTime((int) Limited_life_v2.getPlayerTimeLeft(player));
                                                ctx.getSource().getPlayer().sendSystemMessage(Component.literal(playername +  "'s time: "+ time));

                                            }
                                            return 1;
                                                })
                                        )
                        )
        );
    }


}
