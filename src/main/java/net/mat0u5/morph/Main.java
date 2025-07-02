package net.mat0u5.morph;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.morph.network.NetworkHandlerServer;
import net.mat0u5.morph.registries.ModRegistries;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Main implements ModInitializer {
	public static final String MOD_ID = "morph";
	public static final String MOD_VERSION = "dev-0.0.6";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Nullable
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Morph!");
		ModRegistries.registerModStuff();
		NetworkHandlerServer.registerPackets();
		NetworkHandlerServer.registerServerReceiver();
	}


	public static boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	public static boolean isLogicalSide() {
		if (!isClient()) return true;
		return MainClient.isRunningIntegratedServer();
	}

	public static boolean isClientPlayer(UUID uuid) {
		if (!isClient()) return false;
		return MainClient.isClientPlayer(uuid);
	}
}