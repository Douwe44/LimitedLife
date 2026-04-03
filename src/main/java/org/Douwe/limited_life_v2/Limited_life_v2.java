package org.Douwe.limited_life_v2;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.yaml.snakeyaml.Yaml;

public class Limited_life_v2 implements ModInitializer {
    public static final String MOD_ID = "limited_life_v2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Leaderboard leaderboard;
    public static GlobalTimer currentGlobalTimer;

    static Map<String, UUID> playerNames = new HashMap<>();
    static Map<UUID, Float> playerList = new HashMap<>();
    static ArrayList<ServerPlayer> onlineList = new ArrayList<>();
    static ExecutorService es = Executors.newSingleThreadExecutor();
    static boolean timerIsRunning;
    static Scoreboard scoreboard;
    static MinecraftServer s;
    BoogeymanCommand boogeymanCommand = new BoogeymanCommand();
    GiveKillCommand giveKillCommand = new GiveKillCommand();
    EndSessionCommand endSessionCommand = new EndSessionCommand();
    TimerCommand timerCommand = new TimerCommand();
    GetTimeCommand getTimeCommand = new GetTimeCommand();
    SetTimeCommand setTimeCommand = new SetTimeCommand();
    Config config;

    @Override
    public void onInitialize() {
        Yaml yaml = new Yaml();
        Path dataPath = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "/data.yml");
        Path namePath = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "/names.yml");
        Path path = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        CreateDirectoryIfNotExists(path);
        File configFile = path.resolve("config.yml").toFile();
        CreateFileIfNotExists(configFile, "config.yml");
        config = Config.Load(configFile);


        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            s = server;
            scoreboard = s.getScoreboard();
            leaderboard = new Leaderboard();
            currentGlobalTimer = new GlobalTimer(config);
            if(dataPath.toFile().exists()) {
                try {
                    FileReader reader = new FileReader(dataPath.toFile());
                    Map<String, Double> storage = yaml.load(reader);
                    for(String idString : storage.keySet()){
                        UUID id = UUID.fromString(idString);
                        playerList.put(id, storage.get(idString).floatValue());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(config.enable.testKaas) {
                System.out.println("het werkt niet");
            } else {
                System.out.println("het werkt wel");
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register((server) -> {


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
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> boogeymanCommand.register(dispatcher, config));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> giveKillCommand.register(dispatcher, config));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> endSessionCommand.register(dispatcher, config));
        if(config.enable.testFeature) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> getTimeCommand.register2(dispatcher));
        } else {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> getTimeCommand.register(dispatcher));
        }
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> setTimeCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> timerCommand.register2(dispatcher));

        ServerPlayConnectionEvents.JOIN.register(
                (serverPlayNetworkHandler, packetSender, minecraftServer) -> {
                    ServerPlayer onlinePlayer = serverPlayNetworkHandler.getPlayer();
                    playerJoined(onlinePlayer);
                }
        );
        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, minecraftServer) -> {
                    ServerPlayer onlinePlayer = serverPlayNetworkHandler.getPlayer();
                    playerLeft(onlinePlayer);
                });
        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            if(livingEntity.isAlwaysTicking()) {
                KillsAndDeaths.playerDeath(livingEntity, damageSource, config);
            }
        });
    }


    public void playerJoined(ServerPlayer p) {
        if(!playerList.containsKey(p.getUUID())) { //check if it's a new player
            if(!playerList.isEmpty()) {
                playerList.put(p.getUUID(), averageTime());
                if(timerIsRunning && !currentGlobalTimer.playerHasActiveTimer(p.getUUID())){
                    currentGlobalTimer.startPlayerTimer(p.getUUID());
                }
            } else {
                playerList.put(p.getUUID(), config.numbers.startTime);
            }
        }
        if(!playerNames.containsKey(p.getPlainTextName())){
            playerNames.put(p.getPlainTextName(), p.getUUID());
        }
        onlineList.add(p);
    }
    public void playerLeft(ServerPlayer p) { //if server is stopped this maybe does not work
        onlineList.remove(p);
    }

    public static float getPlayerTimeLeft(UUID id) {
        return playerList.getOrDefault(id, 0f);
    }
    public static String secToTime(int s) {
        int hours = s / 3600;
        int minutes = (s % 3600) / 60;
        int seconds = s % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public float averageTime() {
        float time = 0;
        for(UUID id : playerList.keySet()) {
            time = time + playerList.get(id);
        }
        return time/playerList.size();
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
    private void CreateFileIfNotExists(File file, String useResource) {
        if(file.exists()) return;

        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(useResource)) {
            if(is == null) {
                LOGGER.warn("Failed to find the resource to create file");
                return;
            }
            Files.copy(is, file.toPath());
        } catch (IOException e) {
            LOGGER.warn("Failed to create a file from a resource");
            return;
        }
    }



}
//to do list: waar moet data.yml, float en uuid, and change team offline player without serverplayerentity
//beetje zitten lezen misschien werkt serverplayerentity niet als speler offline is dus dat moet even worden aangepast
//add data
//maak alles aan elkaar
//test
//extra commands for stuf in globaltimer en misshien andere dingen voor meer aanpassingen
//misschien configg om tijden aan te passen en functies
//enchantment table
//resource pack stuf?

