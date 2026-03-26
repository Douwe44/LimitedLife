package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.permission.*;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


import static net.minecraft.server.command.CommandManager.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.onlineList;
import static org.Douwe.limited_life_v2.Limited_life_v2.scoreboard;

import me.lucko.fabric.api.permissions.v0.Permissions;

public class BoogeymanCommand {
    static ArrayList<ServerPlayerEntity> boogeyList = new ArrayList<>();
    public void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("LimitedLife")
                .then(literal("letsBoogey")
                    //.requires((src) -> src.getPermissions().hasPermission(new Permission.Level(PermissionLevel.fromLevel(4))))
                    .requires(Permissions.require("limited_life_v2.command", 4))
                    .executes(ctx -> {
                        boogeyMan(ctx.getSource());
                        return 1;})
                )
        );
    }

    public void boogeyMan(ServerCommandSource source) {
        //source.getServer().getPlayerManager().disconnectAllPlayers();
        int redPlayers = 0;
        for(ServerPlayerEntity p : onlineList){
            if(p.getScoreboardTeam() == scoreboard.getTeam("red")) {
                redPlayers = redPlayers +1;
            }
        }
        int nonRedPlayers = onlineList.size() - redPlayers;
        Random r = new Random();
        int gamble = r.nextInt(100); //0-99
        if(nonRedPlayers < 1) {
            if(redPlayers < 4) {
                System.out.println("only 3 red players left, so no boogeyman");
            } else if(gamble < 50){
                sendBoogeyMessage(-1);
            }
        } else if(nonRedPlayers < 3) {
            if((gamble < 10) && (nonRedPlayers == 2)) {
                sendBoogeyMessage(2);
            } else if(gamble < 60) {
                sendBoogeyMessage(1);
            } else {
                sendBoogeyMessage(0);
            }
        } else {
            if(gamble < 5) {
                sendBoogeyMessage(3);
            } else if(gamble < 30) {
                sendBoogeyMessage(2);
            } else {
                sendBoogeyMessage(1);
            }
        }

    }
    public void sendBoogeyMessage(int cursed) {//add timerstuf
        Collections.shuffle(onlineList);
        timeDelay(1000,"", "Choosing Boogeyman in:", "", "", Formatting.GRAY);
        timeDelay(2000,"", "3", "", "", Formatting.GREEN);
        timeDelay(3000,"", "2", "", "", Formatting.YELLOW);
        timeDelay(4000,"", "1", "", "", Formatting.RED);
        timeDelay(5000,"", "YOU", "", "", Formatting.GREEN);
        timeDelay(7000,"", "ARE", "", "", Formatting.YELLOW);
        if(cursed == -1) {
            boogeyList.add(onlineList.getFirst());
//        } else if(cursed == 3) {
//            chooseBoogey(0);
//            chooseBoogey(1);
//            chooseBoogey(2);
//        } else if(cursed == 2) {
//            chooseBoogey(0);
//            chooseBoogey(1);
//        } else if(cursed == 1){
//            chooseBoogey(0);//deze 3 dingen kan waarschijnlijk makkelijkerker in een keer met for loopie
        } else {
            int i = 0;
            while (i < cursed) {
                if(!(onlineList.get(i).getScoreboardTeam() == scoreboard.getTeam("red") || (boogeyList.contains(onlineList.get(i))))) {
                    boogeyList.add(onlineList.get(i));
                    i = i + 1;
                }
            }
        }

        sendBoogey();
    }
//    public void chooseBoogey(int i) {//kan if(i<list.size, vor geval er iets misgaat
//        if(onlineList.get(i).getScoreboardTeam() == scoreboard.getTeam("red") || (boogeyList.contains(onlineList.get(i)))){
//            chooseBoogey(i+1);
//        } else {
//            boogeyList.add(onlineList.get(i));
//        }
//    }

    public void sendBoogey() {
        for(ServerPlayerEntity p : onlineList) {
            if(boogeyList.contains(p)) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.networkHandler.sendPacket(new TitleFadeS2CPacket(2, 30, 10));
                                p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("THE BOOGEYMAN").formatted(Formatting.RED)));
                                p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("")));
                            }
                        },10000
                );
            } else {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.networkHandler.sendPacket(new TitleFadeS2CPacket(2, 30, 10));
                                p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("NOT The Boogeyman").formatted(Formatting.GREEN)));
                                p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal("")));
                            }
                        },10000
                );
            }


        }
        //boogeyList.clear();
    }
    public void sendAllMessage(String title, String subtitle, String bar, String chat, Formatting color) {
        for(ServerPlayerEntity p : onlineList) {
            p.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 20, 10));
            p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal(title).formatted(color)));
            p.networkHandler.sendPacket(new SubtitleS2CPacket(Text.literal(subtitle).formatted(color)));
            p.sendMessage(Text.literal(bar).formatted(color), true);
            //p.sendMessage(Text.literal(chat).formatted(color), false);
        }
    }
    void timeDelay(long time, String title, String subtitle, String bar, String chat, Formatting color) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    public void run() {
                        sendAllMessage(title, subtitle, bar, chat, color);
                    }
                },time
        );
    }
}
