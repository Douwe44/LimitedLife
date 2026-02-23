package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.permission.*;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.CommandManager;
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

public class BoogeymanCommand {
    //permission kak moet gefixed worden en static stuf
    ArrayList<ServerPlayerEntity> boogeyList = new ArrayList<>();
    public void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("LetsBoogey") //ben denk ik meer dan een uur bezig geweest met zorgen dat alleen OP dit kan maar shit mag echt sitkken
                .requires((src) -> src.getPermissions().hasPermission(new Permission.Level(PermissionLevel.fromLevel(4))))
                .executes(ctx -> {
                    boogeyMan(ctx.getSource());
                    return 1;})); //
    }

    public void boogeyMan(ServerCommandSource source) {
        //source.getServer().getPlayerManager().disconnectAllPlayers();
        int redPlayers = 0;
        for(ServerPlayerEntity p : onlineList){
            if(p.getScoreboardTeam() == scoreboard.getTeam("red")) {
                redPlayers = redPlayers +1;
            }
        }
        //int redPlayers = Limited_life_v2.scoreboard.getTeam("red").getPlayerList().size();
        int nonRedPlayers = onlineList.size() - redPlayers;
        Random r = new Random();
        int gamble = r.nextInt(100); //0-99
        if(nonRedPlayers < 1) {
            if(redPlayers < 4) {
                System.out.println("only 3 players left, so no boogeyman");
            } else if(gamble < 50){
                sendBoogeyMessage(-1);
            }
        } else if(nonRedPlayers < 3) {
            if(gamble < 10) {
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
        }//change this with online list

    }
    public void sendBoogeyMessage(int cursed) {//add timerstuf
        Collections.shuffle(onlineList);
        sendAllMessage("", "Choosing Boogeyman in:", "", "", Formatting.GRAY);
        sendAllMessage("", "3", "", "", Formatting.GREEN);
        sendAllMessage("", "2", "", "", Formatting.YELLOW);
        sendAllMessage("", "1", "", "", Formatting.RED);
        sendAllMessage("", "YOU", "", "", Formatting.GREEN);
        sendAllMessage("", "ARE", "", "", Formatting.YELLOW);
        if(cursed == -1) {
            boogeyList.add(onlineList.getFirst());
        } else if(cursed == 3) {
            chooseBoogey(0);
            chooseBoogey(1);
            chooseBoogey(2);
        } else if(cursed == 2) {
            chooseBoogey(0);
            chooseBoogey(1);
        } else if(cursed == 1){
            chooseBoogey(0);//deze 3 dingen kan waarschijnlijk makkelijkerker in een keer met for loopie
        }
        sendBoogey();
    }
    public void chooseBoogey(int i) {//kan if(i<list.size, vor geval er iets misgaat
        if(onlineList.get(i).getScoreboardTeam() == scoreboard.getTeam("red") || (boogeyList.contains(onlineList.get(i)))){
            chooseBoogey(i+1);
        } else {
            boogeyList.add(onlineList.get(i));
        }
    }

    public void sendBoogey() {
        for(ServerPlayerEntity p : onlineList) {
            if(boogeyList.contains(p)) {
                p.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 20, 10));
                p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("THE BOOGEYMAN").formatted(Formatting.RED)));
            } else {
                p.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 20, 10));
                p.networkHandler.sendPacket(new TitleS2CPacket(Text.literal("NOT The Boogeyman").formatted(Formatting.GREEN)));
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
            p.sendMessage(Text.literal(chat).formatted(color), false);
        }
    }
}
