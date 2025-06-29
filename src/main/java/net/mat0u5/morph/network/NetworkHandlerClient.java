package net.mat0u5.morph.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.mat0u5.morph.Main;
import net.mat0u5.morph.morph.MorphManager;
import net.mat0u5.morph.network.packets.HandshakePayload;
import net.mat0u5.morph.network.packets.StringListPayload;
import net.mat0u5.morph.utils.VersionControl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;

public class NetworkHandlerClient {
    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleHandshake(payload));
        });
        ClientPlayNetworking.registerGlobalReceiver(StringListPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> handleStringListPacket(payload.name(),payload.value()));
        });
    }

    public static void handleStringListPacket(String name, List<String> value) {
        if (name.equalsIgnoreCase("morph")) {
            try {
                String morphUUIDStr = value.get(0);
                UUID morphUUID = UUID.fromString(morphUUIDStr);
                String morphTypeStr = value.get(1);
                EntityType<?> morphType = null;
                if (!morphTypeStr.equalsIgnoreCase("null") && !morphUUIDStr.isEmpty()) {
                    morphType = Registries.ENTITY_TYPE.get(Identifier.of(morphTypeStr));
                }
                if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Received morph packet: {} ({})", morphType, morphUUID);
                MorphManager.setFromPacket(morphUUID, morphType);
            } catch (Exception e) {}
        }
    }

    public static void handleHandshake(HandshakePayload payload) {
        String clientVersionStr = Main.MOD_VERSION;
        String clientCompatibilityStr = VersionControl.clientCmpatibilityMin();

        int clientVersion = VersionControl.getModVersionInt(clientVersionStr);
        int clientCompatibility = VersionControl.getModVersionInt(clientCompatibilityStr);

        HandshakePayload sendPayload = new HandshakePayload(clientVersionStr, clientVersion, clientCompatibilityStr, clientCompatibility);
        ClientPlayNetworking.send(sendPayload);
        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_CLIENT] Sent handshake");
    }
}
