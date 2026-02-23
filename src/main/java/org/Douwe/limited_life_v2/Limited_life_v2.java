package org.Douwe.limited_life_v2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.command.permission.Permissions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Limited_life_v2 implements ModInitializer {
    public static GlobalTimer currentGlobalTimer;
    static Map<ServerPlayerEntity, Float> playerList = new HashMap<>();
    static ArrayList<ServerPlayerEntity> onlineList = new ArrayList<>();
    static ExecutorService es = Executors.newSingleThreadExecutor();
    float dummie = 10;
    float maxTime = 1000;
    boolean timeStarted = false;
    static Scoreboard scoreboard;
    static MinecraftServer s;


    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            s = server;
            scoreboard = s.getScoreboard();
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> BoogeymanCommand.register(dispatcher));
        //command register ofzo vraag mij niet hoe dit werkt. broo ik ben hier zo klaar mee want fabric page slaat nergens op

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
                    });;
                    es.submit(() -> {
                        try {
                            TimeUnit.MILLISECONDS.sleep(2400);
                            onlinePlayer.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 30, 10));
                            onlinePlayer.networkHandler.sendPacket(new TitleS2CPacket(Text.of("NOT the House")));
                            onlinePlayer.networkHandler.sendPacket(new SubtitleS2CPacket(Text.of(" ")));
                            s.getPlayerManager().broadcast(Text.literal("poeh wat een dikzak is gejoined").formatted(Formatting.DARK_GREEN), false );
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                }
        );
        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> {
                    ServerPlayerEntity onlinePlayer = serverPlayNetworkHandler.getPlayer();
                    playerLeft(onlinePlayer);
                });
    }
    public void playerJoined(ServerPlayerEntity p) {
        if(!playerList.containsKey(p)) { //check if it's a new player
            if(timeStarted) {
                playerList.put(p, dummie);
            } else {
                playerList.put(p, maxTime);
            }
        }
        onlineList.add(p);
    }
    public void playerLeft(ServerPlayerEntity p) { //if server is stopped this maybe does not work
        onlineList.remove(p);
    }

    public float getPlayerTimeLeft(ServerPlayerEntity p) {
        if (playerList.containsKey(p)) {
            return playerList.get(p);
        } else return 0f; //persoon niet bestaand
    }
    public void endSession() {
        currentGlobalTimer = null; //is dit nuttig
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
//kas gfietd sss

