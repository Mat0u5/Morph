package net.mat0u5.morph.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.morph.Main;
import net.mat0u5.morph.network.packets.HandshakePayload;
import net.mat0u5.morph.network.packets.StringListPayload;
import net.mat0u5.morph.utils.PlayerUtils;
import net.mat0u5.morph.utils.VersionControl;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.morph.Main.server;

public class NetworkHandlerServer {
    public static final List<UUID> handshakeSuccessful = new ArrayList<>();

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(StringListPayload.ID, StringListPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(HandshakePayload.ID, HandshakePayload.CODEC);

        PayloadTypeRegistry.playC2S().register(StringListPayload.ID, StringListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(HandshakePayload.ID, HandshakePayload.CODEC);
    }
    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(HandshakePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            MinecraftServer server = context.server();
            server.execute(() -> handleHandshakeResponse(player, payload));
        });
    }

    public static void handleHandshakeResponse(ServerPlayerEntity player, HandshakePayload payload) {
        String clientVersionStr = payload.modVersionStr();
        String clientCompatibilityStr = payload.compatibilityStr();

        int clientVersion = payload.modVersion();
        int clientCompatibility = payload.compatibility();


        String serverVersionStr = Main.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
        int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

        //Check if client version is compatible with the server version
        if (clientVersion < serverCompatibility) {
            Text disconnectText = Text.literal("[Morph Mod] Client-Server version mismatch!\n" +
                    "Update the client version to at least version "+serverCompatibilityStr);
            player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
            return;
        }

        //Check if server version is compatible with the client version
        if (serverVersion < clientCompatibility) {
            Text disconnectText = Text.literal("[Morph Mod] Server-Client version mismatch!\n" +
                    "The client version is too new for the server.\n" +
                    "Either update the server, or downgrade the client version to " + serverVersionStr);
            player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
            return;
        }

        if (VersionControl.isDevVersion()) Main.LOGGER.info("[PACKET_SERVER] Received handshake (from "+player.getNameForScoreboard()+"): {"+payload.modVersionStr()+", "+payload.modVersion()+"}");
        handshakeSuccessful.add(player.getUuid());
    }

    /*
        Sending
     */

    public static void sendHandshake(ServerPlayerEntity player) {
        String serverVersionStr = Main.MOD_VERSION;
        String serverCompatibilityStr = VersionControl.serverCompatibilityMin();

        int serverVersion = VersionControl.getModVersionInt(serverVersionStr);
        int serverCompatibility = VersionControl.getModVersionInt(serverCompatibilityStr);

        HandshakePayload payload = new HandshakePayload(serverVersionStr, serverVersion, serverCompatibilityStr, serverCompatibility);
        ServerPlayNetworking.send(player, payload);
        handshakeSuccessful.remove(player.getUuid());
    }

    public static void sendStringListPacket(ServerPlayerEntity player, String name, List<String> value) {
        StringListPayload payload = new StringListPayload(name, value);
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendStringListPackets(String name, List<String> value) {
        for (ServerPlayerEntity player : PlayerUtils.getAllPlayers()) {
            StringListPayload payload = new StringListPayload(name, value);
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void tryKickFailedHandshake(ServerPlayerEntity player) {
        if (server == null) return;
        if (wasHandshakeSuccessful(player)) return;
        Text disconnectText = Text.literal("You must have the §2Morph mod installed§r to play.\n").append(
                Text.literal("§9§nThe Morph mod is available on Modrinth."));
        player.networkHandler.disconnect(new DisconnectionInfo(disconnectText));
    }

    public static boolean wasHandshakeSuccessful(ServerPlayerEntity player) {
        return wasHandshakeSuccessful(player.getUuid());
    }

    public static boolean wasHandshakeSuccessful(UUID uuid) {
        return NetworkHandlerServer.handshakeSuccessful.contains(uuid);
    }
}