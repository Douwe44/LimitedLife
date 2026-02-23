package org.Douwe.limited_life_v2;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GlobalTimer {
    private final ArrayList<ServerPlayerEntity> activeTimerList;

    public void pausePlayerTimer(ServerPlayerEntity p) { activeTimerList.remove(p); }

    public void startPlayerTimer(ServerPlayerEntity p) { activeTimerList.add(p); }

    public boolean playerHasActiveTimer(ServerPlayerEntity p) { return activeTimerList.contains(p); }

    public GlobalTimer() {
        this.activeTimerList = new ArrayList<>();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (Limited_life_v2.currentGlobalTimer == null) {this.cancel(); return;}
                for(ServerPlayerEntity p : Limited_life_v2.playerList.keySet()) {
                    if(activeTimerList.contains(p)) {
                        float timeLeft = Limited_life_v2.playerList.get(p);
                        if(p.isDisconnected()) { //persoon is offline
                            if(timeLeft <= 0) {
                                Limited_life_v2.playerList.replace(p, 0f);
                                activeTimerList.remove(p);
                                p.changeGameMode(GameMode.SPECTATOR);// force spectator
                                //change team do class shit opruimen

                            } else {
                                Limited_life_v2.playerList.replace(p, timeLeft -1.25f); //om dit 1.25 offline punishment
                            }
                        } else { //persoon is online
                            if(timeLeft <= 0) {
                                Limited_life_v2.playerList.replace(p, 0f);
                                activeTimerList.remove(p);
                                p.changeGameMode(GameMode.SPECTATOR);//force spectator
                            } else {
                                Limited_life_v2.playerList.replace(p, timeLeft -1);
                                p.sendMessage(Text.literal(Limited_life_v2.secToTime((int) timeLeft)));//niet in orgineel

                            }
                            //add team color switch
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

