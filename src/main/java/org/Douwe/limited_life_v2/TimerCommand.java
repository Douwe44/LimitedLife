package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.util.UUID;

public class TimerCommand {

    public void register2(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(literal("LimitedLife")
                .then(literal("timer")
                    .requires(Permissions.require("limited_life_v2.command", 4))
                    .then(literal("stop")
                        .executes(ctx -> {
                            for (UUID id : Limited_life_v2.playerList.keySet()) {
                                Limited_life_v2.currentGlobalTimer.pausePlayerTimer(id);
                            };
                            ctx.getSource().getPlayer().sendSystemMessage(Component.literal("you have stopped everyone's time "));
                            Limited_life_v2.timerIsRunning = false;
                            return 1;}
                        )
                        .then(argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                ServerPlayer player =
                                    EntityArgument.getPlayer(ctx, "player");
                                UUID id = player.getUUID();

                                Limited_life_v2.currentGlobalTimer.pausePlayerTimer(id);
                                ctx.getSource().getPlayer().sendSystemMessage(Component.literal("you have stopped " +player.getPlainTextName()+  "'s name"));
                                return 1;}
                            )
                        )
                    )
                    .then(literal("contains")
                        .then(argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                ServerPlayer player =
                                    EntityArgument.getPlayer(ctx, "player");
                                UUID id = player.getUUID();
                                if(Limited_life_v2.currentGlobalTimer.playerHasActiveTimer(id)) {
                                    ctx.getSource().getPlayer().sendSystemMessage(Component.literal(player.getPlainTextName()+  "'s timer is working"));

                                } else {
                                    ctx.getSource().getPlayer().sendSystemMessage(Component.literal(player.getPlainTextName()+  "'s timer is not running"));
                                }
                                return 1;})
                        )
                    )
                    .then(literal("start")
                        .executes(ctx -> {
                            for (UUID id : Limited_life_v2.playerList.keySet()) {
                                if(!Limited_life_v2.currentGlobalTimer.playerHasActiveTimer(id)){
                                    Limited_life_v2.currentGlobalTimer.startPlayerTimer(id);
                                }
                            };
                            ctx.getSource().getPlayer().sendSystemMessage(Component.literal("you have started everyone's time "));
                            Limited_life_v2.timerIsRunning = true;
                            return 1;}
                        )
                        .then(argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                ServerPlayer player =
                                    EntityArgument.getPlayer(ctx, "player");
                                UUID id = player.getUUID();
                                if(!Limited_life_v2.currentGlobalTimer.playerHasActiveTimer(id)) {
                                    Limited_life_v2.currentGlobalTimer.startPlayerTimer(id);
                                }
                                ctx.getSource().getPlayer().sendSystemMessage(Component.literal("you have started " +player.getPlainTextName()+  "'s time"));
                                return 1;}
                            )
                        )
                    )
                )


        );

    }



    // er is deze java code application genaamd ehh omg iets... tel , een etuh iets duits volgens mij ofzo, met het ding kan je muziek live typen
    //stroedel
}
