package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.playerList;

public class SetTimeCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("LimitedLife")
                .then(literal("setTimeOf")
                        .requires(Permissions.require("limited_life_v2.command", 4))
                        .then(argument("player", EntityArgument.player())
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
                                            Float time =
                                                    FloatArgumentType.getFloat(ctx, "time");

                                            playerList.replace(player, time);
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("allPlayers")
                                .then(Commands.argument("time", FloatArgumentType.floatArg())
                                        .executes(ctx -> {
                                            Float time =
                                                    FloatArgumentType.getFloat(ctx, "time");
                                            for (UUID id : playerList.keySet()){
                                                playerList.replace(id, time);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}
