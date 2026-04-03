package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;


import static net.minecraft.commands.Commands.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.*;

import me.lucko.fabric.api.permissions.v0.Permissions;

public class BoogeymanCommand {
    static ArrayList<UUID> boogeyList = new ArrayList<>();
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, Config config){
        dispatcher.register(literal("LimitedLife")
                .then(literal("letsBoogey")
                    //.requires((src) -> src.getPermissions().hasPermission(new Permission.Level(PermissionLevel.fromLevel(4))))
                    .requires(Permissions.require("limited_life_v2.command", 4))
                    .executes(ctx -> {
                        boogeyMan(config);
                        return 1;})
                )
        );
    }

    public void boogeyMan(Config config) {
        //source.getServer().getPlayerManager().disconnectAllPlayers();
        int redPlayers = 0;
        for(ServerPlayer p : onlineList){
            if(p.getTeam() == scoreboard.getPlayerTeam("red")) {
                redPlayers = redPlayers +1;
            }
        }
        int nonRedPlayers = onlineList.size() - redPlayers;
        Random r = new Random();
        int gamble = r.nextInt(100); //0-99
        if(nonRedPlayers < 1) {
            if(redPlayers < 4) {
                System.out.println("only 3 red players left, so no boogeyman");
            } else if(gamble < 50 && config.enable.redBoogeyman){
                sendBoogeyMessage(-1, config);
            }
        } else if(nonRedPlayers < 3) {
            if((gamble < 10) && (nonRedPlayers == 2)) {
                sendBoogeyMessage(2, config);
            } else if(gamble < 60) {
                sendBoogeyMessage(1, config);
            } else {
                sendBoogeyMessage(0, config);
            }
        } else {
            if(gamble < 5) {
                sendBoogeyMessage(3, config);
            } else if(gamble < 30) {
                sendBoogeyMessage(2, config);
            } else {
                sendBoogeyMessage(1, config);
            }
        }

    }
    public void sendBoogeyMessage(int cursed, Config config) {//add timerstuf
        Collections.shuffle(onlineList);
        timeDelay(1000,"", "Choosing Boogeyman in:", "", "", ChatFormatting.GRAY);
        timeDelay(2000,"", "3", "", "", ChatFormatting.GREEN);
        timeDelay(3000,"", "2", "", "", ChatFormatting.YELLOW);
        timeDelay(4000,"", "1", "", "", ChatFormatting.RED);
        timeDelay(5000,"", "YOU", "", "", ChatFormatting.GREEN);
        timeDelay(7000,"", "ARE", "", "", ChatFormatting.YELLOW);
        if(cursed == -1) {
            boogeyList.add(onlineList.getFirst().getUUID());
            if (config.enable.showBoogeyInTerminal){
                System.out.println(onlineList.getFirst().getPlainTextName() + "is the boogeyman");
            }
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
                if(!(onlineList.get(i).getTeam() == scoreboard.getPlayerTeam("red") || (boogeyList.contains(onlineList.get(i).getUUID())))) {
                    boogeyList.add(onlineList.get(i).getUUID());
                    if(config.enable.showBoogeyInTerminal) {
                        System.out.println(onlineList.get(i).getPlainTextName() + "is a boogeyman");
                        //LOGGER.info(onlineList.get(i).getPlainTextName() + "is a boogeyman");
                    }

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
        for(ServerPlayer p : onlineList) {
            if(boogeyList.contains(p.getUUID())) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.connection.send(new ClientboundSetTitlesAnimationPacket(2, 30, 10));
                                p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("THE BOOGEYMAN").withStyle(ChatFormatting.RED)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("")));
                            }
                        },10000
                );
            } else {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                p.connection.send(new ClientboundSetTitlesAnimationPacket(2, 30, 10));
                                p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("NOT The Boogeyman").withStyle(ChatFormatting.GREEN)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("")));
                            }
                        },10000
                );
            }


        }
        //boogeyList.clear();
    }
    public void sendAllMessage(String title, String subtitle, String bar, String chat, ChatFormatting color) {
        for(ServerPlayer p : onlineList) {
            p.connection.send(new ClientboundSetTitlesAnimationPacket(5, 20, 10));
            p.connection.send(new ClientboundSetTitleTextPacket(Component.literal(title).withStyle(color)));
            p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(subtitle).withStyle(color)));
            p.sendSystemMessage(Component.literal(bar).withStyle(color), true);
            //p.sendMessage(Text.literal(chat).formatted(color), false);
        }
    }
    void timeDelay(long time, String title, String subtitle, String bar, String chat, ChatFormatting color) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    public void run() {
                        sendAllMessage(title, subtitle, bar, chat, color);
                    }
                },time
        );
    }
}
