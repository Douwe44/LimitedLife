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

public class EndBoogeyCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, Config config){
        dispatcher.register(literal("LimitedLife")
                .then(literal("endBoogey")
                        .requires(Permissions.require("limited_life_v2.command", 4))
                        .executes(ctx -> {
                            endBoogey(ctx.getSource(), config);
                            return 1;})
                )
        ); //
    }
    public void endBoogey(CommandSourceStack source, Config config) {
        for(ServerPlayer p : onlineList) {
            if (BoogeymanCommand.boogeyList.contains(p.getUUID())) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                //p.connection.send(new ClientboundSetTitlesAnimationPacket(2, 30, 10));
                                p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("YOU HAVE FAILED").withStyle(ChatFormatting.RED)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(" ").withStyle(ChatFormatting.RED)));
                                p.sendSystemMessage(Component.literal(""), true);

                            }
                        }, 500
                );
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                //p.connection.send(new ClientboundSetTitlesAnimationPacket(2, 30, 10));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("YOU SHALL BE PUNISHED").withStyle(ChatFormatting.RED)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("")));
                                p.sendSystemMessage(Component.literal(""), true);


                            }
                        }, 1500
                );
                UUID id = p.getUUID();
                float timeLeft = playerList.get(id);
                if (timeLeft < config.numbers.turnRed) {
                    if (config.enable.killRedBoogey) {
                        playerList.replace(id, 0f);
                    } else {
                        playerList.replace(id, config.numbers.deathPenalty);
                    }
                } else if (timeLeft < config.numbers.turnYellow) {
                    playerList.replace(id, config.numbers.turnRed);
                } else {
                    playerList.replace(id, config.numbers.turnYellow);
                }
                BoogeymanCommand.boogeyList.remove(p.getUUID());
            }
        }
    }
}
