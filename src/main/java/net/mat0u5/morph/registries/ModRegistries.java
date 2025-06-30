package net.mat0u5.morph.registries;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.mat0u5.morph.command.MorphCommand;
import net.mat0u5.morph.events.Events;
import net.mat0u5.morph.utils.TaskScheduler;

public class ModRegistries {
    public static void registerModStuff() {
        registerCommands();
        registerEvents();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(MorphCommand::register);
    }

    private static void registerEvents() {
        Events.register();
        TaskScheduler.registerTickHandler();
    }
}
