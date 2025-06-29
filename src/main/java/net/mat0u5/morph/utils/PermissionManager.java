package net.mat0u5.morph.utils;

import net.mat0u5.morph.Main;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionManager {
    public static boolean isAdmin(ServerPlayerEntity player) {
        if (player == null) return false;
        if (Main.isClientPlayer(player.getUuid())) return true;
        return player.hasPermissionLevel(2);
    }
}
