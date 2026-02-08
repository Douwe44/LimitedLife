package org.Douwe.limited_life_v2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Limited_life_v2 implements ModInitializer {
    public static GlobalTimer currentGlobalTimer;
    static ArrayList<ServerPlayerEntity> playerList = new ArrayList<>();
    static Map<ServerPlayerEntity, Integer> onlineList = new HashMap<>();
    static Map<ServerPlayerEntity, Integer> offlineList = new HashMap<>();
    static ExecutorService es = Executors.newSingleThreadExecutor();
    int dummie = 10;
    int maxTime = 10000;
    boolean timeStarted = false;


    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register(
                (serverPlayNetworkHandler, packetSender, minecraftServer) -> {
                    ServerPlayerEntity onlinePlayer = serverPlayNetworkHandler.getPlayer();
                    playerJoined(onlinePlayer);
                    onlinePlayer.networkHandler.sendPacket( new TitleFadeS2CPacket(5, 20, 10));
                    onlinePlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.of(" ")));
                    onlinePlayer.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("YOU")));
                    es.submit(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(2000);
                            onlinePlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 20, 10));
                            onlinePlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.of(" ")));
                            onlinePlayer.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of("ARE")));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    es.submit(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(2400);
                            onlinePlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 30, 10));
                            onlinePlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.of("NOT the House")));
                            onlinePlayer.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of(" ")));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                }
        );
        ServerPlayConnectionEvents.DISCONNECT.register(
                (serverPlayNetworkHandler, minecraftServer) -> {
                    ServerPlayerEntity offlinePlayer = serverPlayNetworkHandler.getPlayer();
                    playerLeft(offlinePlayer);
                }
        );
    }
    public void playerJoined(ServerPlayerEntity p) {
        if(!playerList.contains(p)) {
            if(timeStarted) {
                onlineList.put(p, dummie);
            } else {
                onlineList.put(p, maxTime);
            }
        } else if(!onlineList.containsKey(p)) {
            onlineList.put(p, offlineList.get(p));
            offlineList.remove(p);
        }
    }
    public void playerLeft(ServerPlayerEntity p) {
        if(!offlineList.containsKey(p)) {
            offlineList.put(p, onlineList.get(p));
            onlineList.remove(p);
        }
    }
    public int getPlayerTimeLeft(ServerPlayerEntity p) {
        if (p.isDisconnected()) {
            return offlineList.get(p);
        } else {
            return onlineList.get(p);
        }
    }
    public void endSession() {
        currentGlobalTimer = null;
    }
    public static String secToTime(int s) {
        int hours = s / 3600;
        int minutes = (s % 3600) / 60;
        int seconds = s % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


}
// Nieuwe spelers die joinen tijdens het spel, krijgen gemiddelde tijd van spelende mensen -half uur
// Oude spelers die tijdens een sessie offline zijn, hun timer gaat sneller naar beneden x1.25.
// na een sessie gaat bij de spelers die de hele sessie niet online zijn geweest (deaths/2x aantal (online?) spelers) eraf
//kas

