package org.Douwe.limited_life_v2;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
                //if (LimitedLife.currentGlobalTimerTask == null) {this.cancel(); return;}
                for(ServerPlayerEntity p : Limited_life_v2.playerList) {
                    if(activeTimerList.contains(p)) {
                        if(p.isDisconnected()) {
                            int timeLeft = Limited_life_v2.offlineList.get(p);
                            if(timeLeft <= 0) {
                                Limited_life_v2.offlineList.replace(p, 0);
                                activeTimerList.remove(p); //en force spectator
                            } else {
                                Limited_life_v2.offlineList.replace(p, timeLeft -1); //om dit 1.25 te maken moet het niet een int zijn, super slim
                            }
                        } else {
                            int timeLeft = Limited_life_v2.onlineList.get(p);
                            if(timeLeft <= 0) {
                                Limited_life_v2.onlineList.replace(p, 0);
                                activeTimerList.remove(p); //en force spectator
                            } else {
                                Limited_life_v2.onlineList.replace(p, timeLeft -1);
                                p.sendMessage(Text.literal(Limited_life_v2.secToTime(timeLeft)));
                            }
                        }
                    }
                }
            }
        };
    }
}
