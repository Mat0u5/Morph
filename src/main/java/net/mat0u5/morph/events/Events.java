package net.mat0u5.morph.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.morph.Main;
import net.mat0u5.morph.morph.MorphManager;
import net.mat0u5.morph.network.NetworkHandlerServer;
import net.mat0u5.morph.utils.PlayerUtils;
import net.mat0u5.morph.utils.TaskScheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class Events {
    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStarting);
        ServerLifecycleEvents.SERVER_STARTED.register(Events::onServerStart);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> onPlayerDisconnect(handler.getPlayer()));
        ServerTickEvents.END_SERVER_TICK.register(Events::onServerTickEnd);
    }

    private static void onPlayerJoin(ServerPlayerEntity player) {
        try {
            playerStartJoining(player);
            MorphManager.onPlayerJoin(player);
        } catch(Exception e) {
            Main.LOGGER.error(e.getMessage());}
    }

    private static void onPlayerFinishJoining(ServerPlayerEntity player) {
        try {
            TaskScheduler.scheduleTask(20, () -> {
                NetworkHandlerServer.tryKickFailedHandshake(player);
                PlayerUtils.resendCommandTree(player);
            });
            MorphManager.onPlayerDisconnect(player);
            MorphManager.syncToPlayer(player);
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onPlayerDisconnect(ServerPlayerEntity player) {
        try {

        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onServerStarting(MinecraftServer server) {
        try {
            Main.server = server;
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onServerStart(MinecraftServer server) {
        try {
            Main.server = server;
        } catch(Exception e) {Main.LOGGER.error(e.getMessage());}
    }

    private static void onServerTickEnd(MinecraftServer server) {
        try {
            if (!Main.isLogicalSide()) return;
            checkPlayerFinishJoiningTick();
        }catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    /*
        Non-events
     */
    public static final List<UUID> joiningPlayers = new ArrayList<>();
    private static final Map<UUID, Vec3d> joiningPlayersPos = new HashMap<>();
    private static final Map<UUID, Float> joiningPlayersYaw = new HashMap<>();
    private static final Map<UUID, Float> joiningPlayersPitch = new HashMap<>();
    public static void playerStartJoining(ServerPlayerEntity player) {
        NetworkHandlerServer.sendHandshake(player);
        joiningPlayers.add(player.getUuid());
        joiningPlayersPos.put(player.getUuid(), player.getPos());
        joiningPlayersYaw.put(player.getUuid(), player.getYaw());
        joiningPlayersPitch.put(player.getUuid(), player.getPitch());
    }
    public static void checkPlayerFinishJoiningTick() {
        for (Map.Entry<UUID, Vec3d> entry : joiningPlayersPos.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getPos().equals(entry.getValue())) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUuid());
            return;
        }
        //Yaw
        for (Map.Entry<UUID, Float> entry : joiningPlayersYaw.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getYaw() == entry.getValue()) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUuid());
            return;
        }
        //Pitch
        for (Map.Entry<UUID, Float> entry : joiningPlayersPitch.entrySet()) {
            UUID uuid = entry.getKey();
            ServerPlayerEntity player = PlayerUtils.getPlayer(uuid);
            if (player == null) continue;
            if (player.getPitch() == entry.getValue()) continue;
            onPlayerFinishJoining(player);
            finishedJoining(player.getUuid());
            return;
        }

    }
    public static void finishedJoining(UUID uuid) {
        joiningPlayers.remove(uuid);
        joiningPlayersPos.remove(uuid);
        joiningPlayersYaw.remove(uuid);
        joiningPlayersPitch.remove(uuid);
    }
}
