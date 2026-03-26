package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.onlineList;
import static org.Douwe.limited_life_v2.Limited_life_v2.playerList;

public class EndSessionCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, Config config){
        dispatcher.register(literal("endSession")
                .requires(Permissions.require("limited_life_v2.command", 4))
                .executes(ctx -> {
                    endSession(ctx.getSource(), config);
                    return 1;})); //
    }
    public void endSession(ServerCommandSource source, Config config) {
        for(ServerPlayerEntity p : onlineList) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        public void run() {
                            p.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 20, 10));
                            p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("")));
                            p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("Session has ended")));
                            p.sendMessage(Text.literal("You will be kicked in a moment <3"), true );

                        }
                    },500
            );
            if(BoogeymanCommand.boogeyList.contains(p)) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("YOU HAVE FAILED").formatted(Formatting.RED)));
                                p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("TO CURE YOURSELF")));
                                p.sendMessage(Text.literal(""), true );

                            }
                        },2000
                );
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("YOU SHALL BE PUNISHED").formatted(Formatting.RED)));
                                p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("")));
                                p.sendMessage(Text.literal(""), true );


                            }
                        },4000
                );
                UUID id = p.getUuid();
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
                            source.getServer().getPlayerManager().disconnectAllPlayers();//kick players
                            Limited_life_v2.currentGlobalTimer = null;//end timer
                            //source.getServer().stop(false); //true or false?, neem aan dat true beter is
                        }
                    },10000
            );

        }
    }
}
