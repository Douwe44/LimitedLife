package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.onlineList;
import static org.Douwe.limited_life_v2.Limited_life_v2.playerList;

public class EndSessionCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, Config config){
        dispatcher.register(literal("LimitedLife")
                .then(literal("endSession")
                    .requires(Permissions.require("limited_life_v2.command", 4))
                    .executes(ctx -> {
                        endSession(ctx.getSource(), config);
                        return 1;})
                )
        ); //
    }
    public void endSession(CommandSourceStack source, Config config) {
        for(ServerPlayer p : onlineList) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        public void run() {
                            p.connection.send(new ClientboundSetTitlesAnimationPacket(5, 20, 10));
                            p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("")));
                            p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("Session has ended")));
                            p.sendSystemMessage(Component.literal("You will be kicked in a moment <3"), true );

                        }
                    },500
            );
            if(BoogeymanCommand.boogeyList.contains(p)) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("YOU HAVE FAILED").withStyle(ChatFormatting.RED)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("TO CURE YOURSELF")));
                                p.sendSystemMessage(Component.literal(""), true );

                            }
                        },2000
                );
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("YOU SHALL BE PUNISHED").withStyle(ChatFormatting.RED)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("")));
                                p.sendSystemMessage(Component.literal(""), true );


                            }
                        },4000
                );
                UUID id = p.getUUID();
                float timeLeft = playerList.get(id);
                if(timeLeft < config.numbers.turnRed) {
                    if(config.enable.killRedBoogey) {
                        playerList.replace(id, 0f);
                    } else {
                        playerList.replace(id, config.numbers.deathPenalty);
                    }
                } else if(timeLeft < config.numbers.turnYellow) {
                    playerList.replace(id, config.numbers.turnRed);
                } else {
                    playerList.replace(id, config.numbers.turnYellow);
                }
                BoogeymanCommand.boogeyList.remove(p);
            }
            Limited_life_v2.timerIsRunning = false;
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        public void run() {
                            source.getServer().getPlayerList().removeAll();//kick players
                            Limited_life_v2.currentGlobalTimer = null;//end timer
                            //source.getServer().stop(false); //true or false?, neem aan dat true beter is
                        }
                    },10000
            );

        }
    }
}
