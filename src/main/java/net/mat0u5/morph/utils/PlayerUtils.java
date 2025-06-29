package net.mat0u5.morph.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

import static net.mat0u5.morph.Main.server;

public class PlayerUtils {

    public static List<ServerPlayerEntity> getAllPlayers() {
        if (server == null) return new ArrayList<>();
        return new ArrayList<>(server.getPlayerManager().getPlayerList());
    }

    public static ServerPlayerEntity getPlayer(String name) {
        if (server == null || name == null) return null;
        return server.getPlayerManager().getPlayer(name);
    }

    public static ServerPlayerEntity getPlayer(UUID uuid) {
        if (server == null || uuid == null) return null;
        return server.getPlayerManager().getPlayer(uuid);
    }

    public static void resendCommandTree(ServerPlayerEntity player) {
        if (player == null) return;
        if (player.getServer() == null) return;
        player.getServer().getCommandManager().sendCommandTree(player);
    }

    public static void resendCommandTrees() {
        for (ServerPlayerEntity player : getAllPlayers()) {
            resendCommandTree(player);
        }
    }

    public static ServerWorld getServerWorld(ServerPlayerEntity player) {
        //? if <= 1.21.5 {
        return player.getServerWorld();
        //?} else {
        /*return player.getWorld();
         *///?}
    }
}
