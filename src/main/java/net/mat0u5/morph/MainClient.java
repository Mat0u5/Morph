package net.mat0u5.morph;

import net.fabricmc.api.ClientModInitializer;
import net.mat0u5.morph.network.NetworkHandlerClient;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

public class MainClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NetworkHandlerClient.registerClientReceiver();
    }

    public static boolean isRunningIntegratedServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        return client.isIntegratedServerRunning();
    }

    public static boolean isClientPlayer(UUID uuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        return client.player.getUuid().equals(uuid);
    }
}
