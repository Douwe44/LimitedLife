package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GiveKillCommand {
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, Config config){
        dispatcher.register(literal("LimitedLife")
                .then(literal("giveKillTo")
                        .requires(Permissions.require("limited_life_v2.command", 4))
                        .then(argument("player", EntityArgumentType.player())
                            .executes(ctx -> {
                                ServerPlayerEntity player =
                                    EntityArgumentType.getPlayer(ctx, "player");
                                giveKill(player, config);
                                return 1;})
                        )
                )
        );
    }
    public void giveKill(ServerPlayerEntity p, Config config) {
        float timeLeft = Limited_life_v2.playerList.get(p.getUuid());
        Limited_life_v2.playerList.replace(p.getUuid(), timeLeft + config.numbers.killReward);
        if(BoogeymanCommand.boogeyList.contains(p)) {
            //misschien nog een kadootje
            p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("YOU ARE CURED").formatted(Formatting.GREEN)));
            BoogeymanCommand.boogeyList.remove(p);
        }
    }
}
