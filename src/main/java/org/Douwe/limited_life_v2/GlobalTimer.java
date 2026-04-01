package org.Douwe.limited_life_v2;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class GlobalTimer {
    Config config;
    private final ArrayList<UUID> activeTimerList;

    public void pausePlayerTimer(UUID id) { activeTimerList.remove(id); }

    public void startPlayerTimer(UUID id) { activeTimerList.add(id); }

    public boolean playerHasActiveTimer(UUID id) { return activeTimerList.contains(id); }

    public GlobalTimer(Config config) {
        this.activeTimerList = new ArrayList<>();
        this.config = config;
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (Limited_life_v2.currentGlobalTimer == null) {this.cancel(); return;}
                for(UUID id : Limited_life_v2.playerList.keySet()) {
                    if(activeTimerList.contains(id)) {
                        float timeLeft = Limited_life_v2.playerList.get(id);
                        ServerPlayerEntity p = Limited_life_v2.s.getPlayerManager().getPlayer(id);
                        if(p == null || !(Limited_life_v2.onlineList.contains(p)) ) { //persoon is offline  ... maybe change online list to uuid
                            if(timeLeft <= 0) {
                                Limited_life_v2.playerList.replace(id, 0f);
                                activeTimerList.remove(id);
                                //p.changeGameMode(GameMode.SPECTATOR);// force spectator
                                //Limited_life_v2.leaderboard.changeTeam(p, timeLeft);

                            } else {
                                Limited_life_v2.playerList.replace(id, timeLeft -1.25f); //om dit 1.25 offline punishment
                            }
                        } else { //persoon is online
                            if(timeLeft <= 0) {
                                Limited_life_v2.playerList.replace(id, 0f);
                                activeTimerList.remove(id);
                                p.changeGameMode(GameMode.SPECTATOR);//force spectator
                            } else {
                                Limited_life_v2.playerList.replace(id, timeLeft -1);
                                if(config.enable.showTimeToPlayer){
                                    p.sendMessage(Text.literal(Limited_life_v2.secToTime((int) timeLeft)), true);//niet in orgineel
                                }

                            }
                            Limited_life_v2.leaderboard.changeTeam(p, timeLeft, config);
                        }
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
}
//Andere guy extra if( p != null) protectie voor als er iets geks gebeurd, kan nog toegevoegd worden.
// changeTeam gebeurd alleen als iemand online is, dat kan klein beetje probleem zorgen maar als het goed is niet heel erg
//

