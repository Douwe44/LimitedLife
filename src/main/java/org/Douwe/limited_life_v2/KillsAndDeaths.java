package org.Douwe.limited_life_v2;

import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;


public class KillsAndDeaths {
    public static void playerDeath(LivingEntity d, DamageSource k, Config config){
        if(Limited_life_v2.currentGlobalTimer == null) return;
        ServerPlayer deadPlayer = (ServerPlayer)d;
        UUID idDead = deadPlayer.getUUID();
        if(!Limited_life_v2.currentGlobalTimer.playerHasActiveTimer(idDead)) return;
        float timeLeft = Limited_life_v2.playerList.get((idDead));
        Limited_life_v2.playerList.replace(idDead, timeLeft - config.numbers.deathPenalty);
        if(k.getEntity() == null) return;
        if(k.getEntity().isAlwaysTicking()) {
            ServerPlayer killer = (ServerPlayer) k.getEntity();
            timeLeft = Limited_life_v2.playerList.get(killer.getUUID());
            Limited_life_v2.playerList.replace(killer.getUUID(), timeLeft + config.numbers.killReward);
            if(BoogeymanCommand.boogeyList.contains(killer)) {
                //misschien nog een kadootje
                killer.connection.send(new ClientboundSetTitleTextPacket(Component.literal("YOU ARE CURED").withStyle(ChatFormatting.GREEN)));
                BoogeymanCommand.boogeyList.remove(killer);
            }
        }
    }

}
