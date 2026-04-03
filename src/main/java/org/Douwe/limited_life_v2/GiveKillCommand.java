package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GiveKillCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, Config config){
        dispatcher.register(literal("LimitedLife")
                .then(literal("giveKillTo")
                        .requires(Permissions.require("limited_life_v2.command", 4))
                        .then(argument("player", EntityArgument.player())
                            .executes(ctx -> {
                                ServerPlayer player =
                                    EntityArgument.getPlayer(ctx, "player");
                                giveKill(player, config);
                                return 1;})
                        )
                )
        );
    }
    public void giveKill(ServerPlayer p, Config config) {
        float timeLeft = Limited_life_v2.playerList.get(p.getUUID());
        Limited_life_v2.playerList.replace(p.getUUID(), timeLeft + config.numbers.killReward);
        if(BoogeymanCommand.boogeyList.contains(p.getUUID())) {
            Limited_life_v2.playerList.replace(p.getUUID(), timeLeft + config.numbers.extraBoogeyReward);
            p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("YOU ARE CURED").withStyle(ChatFormatting.GREEN)));
            BoogeymanCommand.boogeyList.remove(p.getUUID());
        }
    }
}
