package org.Douwe.limited_life_v2;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import static org.Douwe.limited_life_v2.Limited_life_v2.s;
import static org.Douwe.limited_life_v2.Limited_life_v2.scoreboard;

public class Leaderboard {
    private Team dead;
    private Team red;
    private Team yellow;
    private Team green;


    public Leaderboard() {
        Team dead = scoreboard.addTeam("dead");
        dead.setColor(Formatting.GRAY);
        dead.setShowFriendlyInvisibles(false);
        dead.setFriendlyFireAllowed(true);

        Team red = scoreboard.addTeam("red");
        red.setColor(Formatting.RED);
        red.setShowFriendlyInvisibles(false);
        red.setFriendlyFireAllowed(true);

        Team yellow = scoreboard.addTeam("yellow");
        yellow.setColor(Formatting.YELLOW);
        yellow.setShowFriendlyInvisibles(false);
        yellow.setFriendlyFireAllowed(true);

        Team green = scoreboard.addTeam("green");
        green.setColor(Formatting.GREEN);
        green.setShowFriendlyInvisibles(false);
        green.setFriendlyFireAllowed(true);
    }
    public void changeTeam(ServerPlayerEntity p, float timeLeft) {
        String name = p.getStringifiedName();
        if(timeLeft <= 0) {
            scoreboard.addScoreHolderToTeam(name, dead);
            p.changeGameMode(GameMode.SPECTATOR);
            s.getPlayerManager().broadcast(Text.literal(name +"'s tijd is op, L bozo!").formatted(Formatting.RED), false );
            if(!p.isDisconnected()) {
                LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, p.getEntityWorld());
                lightning.setCosmetic(true);
                lightning.setPos(p.getX(), p.getY(), p.getZ());
                p.getEntityWorld().spawnEntity(lightning);
            }
            //add to a deadlist(inSaveData) so when next session player does not get added to active list
        } else if(timeLeft < 14400) {//set time here for red team, maybe config
            if(!red.getPlayerList().contains(name)) {
                scoreboard.addScoreHolderToTeam(name, red);
            }
        } else if(timeLeft < 28800) {//set time here for yellow team, maybe config
            if(!yellow.getPlayerList().contains(name)) { //oke na wat testen en verward zijn volgens mij kunnen er geen dupes zijn dus dit kan gewoon weg, nou wacht kost minder tijd laat maar
                scoreboard.addScoreHolderToTeam(name, yellow);
            }
        } else {
            if(!green.getPlayerList().contains(name)) {
                scoreboard.addScoreHolderToTeam(name, green);
            }
        }
    }
}

