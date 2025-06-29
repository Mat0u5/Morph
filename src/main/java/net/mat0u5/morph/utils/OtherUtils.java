package net.mat0u5.morph.utils;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class OtherUtils {
    public static void sendCommandFeedback(ServerCommandSource source, Text text) {
        if (source == null || text == null) return;
        source.sendFeedback(() -> text, true);
    }

    public static void sendCommandFeedbackQuiet(ServerCommandSource source, Text text) {
        if (source == null || text == null) return;
        source.sendFeedback(() -> text, false);
    }
}
