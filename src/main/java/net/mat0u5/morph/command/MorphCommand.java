package net.mat0u5.morph.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.morph.morph.Morph;
import net.mat0u5.morph.morph.MorphManager;
import net.mat0u5.morph.utils.OtherUtils;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.mat0u5.morph.utils.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class MorphCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                literal("morph")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .then(argument("entity", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                                .suggests((context, builder) -> CommandSource.suggestMatching(Morph.getAvailableMorphs(), builder))
                                .executes(context -> setMorph(
                                        context.getSource(), context.getSource().getPlayer(), Morph.getLivingEntityType(context, "entity").value()
                                ))
                        )
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("entity", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, RegistryKeys.ENTITY_TYPE))
                                        .suggests((context, builder) -> CommandSource.suggestMatching(Morph.getAvailableMorphs(), builder))
                                        .executes(context -> setMorph(
                                                context.getSource(), EntityArgumentType.getPlayer(context,"player"), Morph.getLivingEntityType(context, "entity").value()
                                        ))
                                )
                        )
        );
        dispatcher.register(
                literal("unmorph")
                        .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                        .executes(context -> setMorph(
                                context.getSource(), context.getSource().getPlayer(), null)
                        )
        );
    }

    public static int setMorph(ServerCommandSource source, ServerPlayerEntity target, EntityType<?> morph) {
        if (target == null) return -1;

        MorphManager.setMorph(target, morph);

        if (morph == null) {
            OtherUtils.sendCommandFeedback(source, Text.literal("Un-Morphing ").append(target.getStyledDisplayName()));
            return 1;
        }

        OtherUtils.sendCommandFeedback(source, Text.literal("Morphing ").append(target.getStyledDisplayName()).append(" into " + morph.getName().getString()));
        return 1;
    }
}
