package org.Douwe.limited_life_v2;

import static org.Douwe.limited_life_v2.Limited_life_v2.s;
import static org.Douwe.limited_life_v2.Limited_life_v2.scoreboard;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

public class Leaderboard {
    private final PlayerTeam dead;
    private final PlayerTeam red;
    private final PlayerTeam yellow;
    private final PlayerTeam green;


    public Leaderboard() {
        dead = scoreboard.addPlayerTeam("dead");
        dead.setColor(ChatFormatting.GRAY);
        dead.setSeeFriendlyInvisibles(false);
        dead.setAllowFriendlyFire(true);

        red = scoreboard.addPlayerTeam("red");
        red.setColor(ChatFormatting.RED);
        red.setSeeFriendlyInvisibles(false);
        red.setAllowFriendlyFire(true);

        yellow = scoreboard.addPlayerTeam("yellow");
        yellow.setColor(ChatFormatting.YELLOW);
        yellow.setSeeFriendlyInvisibles(false);
        yellow.setAllowFriendlyFire(true);

        green = scoreboard.addPlayerTeam("green");
        green.setColor(ChatFormatting.GREEN);
        green.setSeeFriendlyInvisibles(false);
        green.setAllowFriendlyFire(true);
    }
    public void changeTeam(ServerPlayer p, float timeLeft, Config config) {
        String name = p.getPlainTextName();
        if(timeLeft <= 0) {
            scoreboard.addPlayerToTeam(name, dead);
            p.setGameMode(GameType.SPECTATOR);
            s.getPlayerList().broadcastSystemMessage(Component.literal(name +"'s tijd is op, L bozo!").withStyle(ChatFormatting.RED), false );
            if(!p.hasDisconnected()) {
                LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, p.level());
                lightning.setVisualOnly(true);
                lightning.setPosRaw(p.getX(), p.getY(), p.getZ());
                p.level().addFreshEntity(lightning);
            }
            //add to a deadlist(inSaveData) so when next session player does not get added to active list
        } else if(timeLeft < config.numbers.turnRed) {//set time here for red team, maybe config
            if(!red.getPlayers().contains(name)) {
                scoreboard.addPlayerToTeam(name, red);
            }
        } else if(timeLeft < config.numbers.turnYellow) {//set time here for yellow team, maybe config
            if(!yellow.getPlayers().contains(name)) { //oke na wat testen en verward zijn volgens mij kunnen er geen dupes zijn dus dit kan gewoon weg, nou wacht kost minder tijd laat maar
                scoreboard.addPlayerToTeam(name, yellow);
            }
        } else {
            if(!green.getPlayers().contains(name)) {
                scoreboard.addPlayerToTeam(name, green);
            }
        }
    }
}

