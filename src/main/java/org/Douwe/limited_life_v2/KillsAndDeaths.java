package org.Douwe.limited_life_v2;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.UUID;


public class KillsAndDeaths {
    public static void playerDeath(LivingEntity d, DamageSource k, Config config){
        if(Limited_life_v2.currentGlobalTimer == null) return;
        ServerPlayerEntity deadPlayer = (ServerPlayerEntity)d;
        UUID idDead = deadPlayer.getUuid();
        if(!Limited_life_v2.currentGlobalTimer.playerHasActiveTimer(idDead)) return;
        float timeLeft = Limited_life_v2.playerList.get((idDead));
        Limited_life_v2.playerList.replace(idDead, timeLeft - config.numbers.deathPenalty);
        if(k.getAttacker() == null) return;
        if(k.getAttacker().isPlayer()) {
            ServerPlayerEntity killer = (ServerPlayerEntity) k.getAttacker();
            timeLeft = Limited_life_v2.playerList.get(killer.getUuid());
            Limited_life_v2.playerList.replace(killer.getUuid(), timeLeft + config.numbers.killReward);
            if(BoogeymanCommand.boogeyList.contains(killer)) {
                //misschien nog een kadootje
                killer.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("YOU ARE CURED").formatted(Formatting.GREEN)));
                BoogeymanCommand.boogeyList.remove(killer);
            }
        }
    }

}
