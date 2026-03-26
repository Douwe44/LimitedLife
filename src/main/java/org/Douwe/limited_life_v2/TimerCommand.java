package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import net.minecraft.text.Text;

import java.util.UUID;

public class TimerCommand {

    public void register2(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("timer")
                .requires(Permissions.require("limited_life_v2.command", 4))
                .then(literal("stop")
                    .executes(ctx -> {
                        for (UUID id : Limited_life_v2.playerList.keySet()) {
                            Limited_life_v2.currentGlobalTimer.pausePlayerTimer(id);
                        };
                        ctx.getSource().getPlayer().sendMessage(Text.literal("you have stopped everyone's time "));
                        Limited_life_v2.timerIsRunning = false;
                        return 1;}
                    )
                    .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity player =
                                EntityArgumentType.getPlayer(ctx, "player");
                            UUID id = player.getUuid();

                            Limited_life_v2.currentGlobalTimer.pausePlayerTimer(id);
                            ctx.getSource().getPlayer().sendMessage(Text.literal("you have stopped " +player.getStringifiedName()+  "'s name"));
                            return 1;}
                        )
                    )
                )
                .then(literal("contains")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity player =
                                EntityArgumentType.getPlayer(ctx, "player");
                            UUID id = player.getUuid();
                            if(Limited_life_v2.currentGlobalTimer.playerHasActiveTimer(id)) {
                                ctx.getSource().getPlayer().sendMessage(Text.literal(player.getStringifiedName()+  "'s timer is working"));

                            } else {
                                ctx.getSource().getPlayer().sendMessage(Text.literal(player.getStringifiedName()+  "'s timer is not running"));
                            }
                            return 1;})
                    )
                )
                .then(literal("start")
                    .executes(ctx -> {
                        for (UUID id : Limited_life_v2.playerList.keySet()) {
                            Limited_life_v2.currentGlobalTimer.startPlayerTimer(id);
                        };
                        ctx.getSource().getPlayer().sendMessage(Text.literal("you have started everyone's time "));
                        Limited_life_v2.timerIsRunning = true;
                        return 1;}
                    )
                    .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity player =
                                EntityArgumentType.getPlayer(ctx, "player");
                            UUID id = player.getUuid();
                            Limited_life_v2.currentGlobalTimer.startPlayerTimer(id);
                            ctx.getSource().getPlayer().sendMessage(Text.literal("you have started " +player.getStringifiedName()+  "'s name"));
                            return 1;}
                        )
                    )
                )
                .then(literal("of")
                    .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity player =
                                EntityArgumentType.getPlayer(ctx, "player");
                            UUID id = player.getUuid();
                            String time = Limited_life_v2.secToTime((int) Limited_life_v2.getPlayerTimeLeft(id));
                            ctx.getSource().getPlayer().sendMessage(Text.literal(player.getStringifiedName()+  "'s time: "+ time));
                            return 1;})
                    )
                )

        );

    }



    // er is deze java code application genaamd ehh omg iets... tel , een etuh iets duits volgens mij ofzo, met het ding kan je muziek live typen
    //stroedel
}
