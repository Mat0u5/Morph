package net.mat0u5.morph.registries;

import net.mat0u5.morph.events.Events;

public class ModRegistries {
    public static void registerModStuff() {
        registerCommands();
        registerEvents();
    }

    private static void registerCommands() {
        //CommandRegistrationCallback.EVENT.register(MorphCommand::register);
    }

    private static void registerEvents() {
        Events.register();
    }
}
