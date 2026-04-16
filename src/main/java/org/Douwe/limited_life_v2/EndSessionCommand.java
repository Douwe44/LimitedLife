package org.Douwe.limited_life_v2;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.minecraft.commands.Commands.literal;
import static org.Douwe.limited_life_v2.Limited_life_v2.*;

public class EndSessionCommand {
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, Config config, GlobalTimer timer){
        dispatcher.register(literal("LimitedLife")
                .then(literal("endSession")
                    .requires(Permissions.require("limited_life_v2.command", 4))
                    .executes(ctx -> {
                        endSession(ctx.getSource(), config, timer);
                        return 1;})
                )
        ); //
    }
    public void endSession(CommandSourceStack source, Config config, GlobalTimer timerkaas) {
        Path dataPath = FabricLoader.getInstance().getConfigDir().resolve(Limited_life_v2.MOD_ID + "/data.yml");
        Path namePath = FabricLoader.getInstance().getConfigDir().resolve(Limited_life_v2.MOD_ID + "/names.yml");
        Path path = FabricLoader.getInstance().getConfigDir().resolve(Limited_life_v2.MOD_ID);
        for(ServerPlayer p : onlineList) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        public void run() {
                            p.connection.send(new ClientboundSetTitlesAnimationPacket(5, 20, 10));
                            p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("")));
                            p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("Session has ended")));
                            p.sendSystemMessage(Component.literal("You will be kicked in a moment <3"), true );

                        }
                    },500
            );
            if(BoogeymanCommand.boogeyList.contains(p.getUUID())) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                //p.connection.send(new ClientboundSetTitlesAnimationPacket(2, 30, 10));
                                p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("YOU HAVE FAILED").withStyle(ChatFormatting.RED)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(" ").withStyle(ChatFormatting.RED)));
                                p.sendSystemMessage(Component.literal(""), true);

                            }
                        }, 1500
                );
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            public void run() {
                                //p.connection.send(new ClientboundSetTitlesAnimationPacket(2, 30, 10));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("YOU SHALL BE PUNISHED").withStyle(ChatFormatting.RED)));
                                p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("")));
                                p.sendSystemMessage(Component.literal(""), true);


                            }
                        }, 2500
                );
                UUID id = p.getUUID();
                float timeLeft = playerList.get(id);
                if(timeLeft < config.numbers.turnRed) {
                    if(config.enable.killRedBoogey) {
                        playerList.replace(id, 0f);
                    } else {
                        playerList.replace(id, config.numbers.deathPenalty);
                    }
                } else if(timeLeft < config.numbers.turnYellow) {
                    playerList.replace(id, config.numbers.turnRed);
                } else {
                    playerList.replace(id, config.numbers.turnYellow);
                }
                BoogeymanCommand.boogeyList.remove(p.getUUID());
            }
        }
        Limited_life_v2.timerIsRunning = false;
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    public void run() {
                        source.getServer().getPlayerList().removeAll();//kick players
                        for(UUID player : playerList.keySet()) {
                            timerkaas.pausePlayerTimer(player);
                        }
                        //Limited_life_v2.currentGlobalTimer = null;//end timer
                        CreateDirectoryIfNotExists(path);
                        Yaml yaml2 = new Yaml();
                        Map<String, Float> data = new HashMap<>();
                        for (UUID id : playerList.keySet()) {
                            data.put(id.toString(), playerList.get(id));
                        }
                        playerList.clear();
                        try {
                            FileWriter writer = new FileWriter(dataPath.toFile());
                            yaml2.dump(data, writer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        Yaml yaml3 = new Yaml();
                        Map<String, String> nameData = new HashMap<>();
                        for (String name : playerNames.keySet()) {
                            nameData.put(name, playerNames.get(name).toString());
                        }
                        playerNames.clear();
                        try {
                            FileWriter writer = new FileWriter(namePath.toFile());
                            yaml3.dump(nameData, writer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        //source.getServer().stop(false); //true or false?, neem aan dat true beter is
                    }
                },10000
        );
    }
    private void CreateDirectoryIfNotExists(Path folder) {
        try {
            if(!folder.toFile().exists()) {
                if(!folder.toFile().mkdir()) {
                    LOGGER.warn("Failed to create a directory (mkdir)");
                    //disabled = true;
                    return;
                }
            }
        } catch(Exception exception) {
            LOGGER.warn("Failed to create a directory");
            //disabled = true;
            return;
        }
    }
}
