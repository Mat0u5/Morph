package net.mat0u5.morph.morph;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.mat0u5.morph.interfaces.IMorph;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.morph.Main.server;

public class Morph {

    private static final DynamicCommandExceptionType NOT_LIVING_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.stringifiedTranslatable("entity.not_living", new Object[]{id}));

    private static List<String> loadedMorphs = new ArrayList<>();

    public static List<String> getAvailableMorphs() {
        if (!loadedMorphs.isEmpty()) return loadedMorphs;
        if (server == null) return loadedMorphs;
        List<String> newMorphs = new ArrayList<>();

        for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            try {
                //? if <= 1.21 {
                Entity entity = entityType.create(server.getOverworld());
                //?} else {
                /*Entity entity = entityType.create(server.getOverworld(), SpawnReason.COMMAND);
                 *///?}
                if (entity != null) ((IMorph) entity).setFromMorph(true);
                if (entity instanceof LivingEntity) {
                    newMorphs.add(Registries.ENTITY_TYPE.getId(entityType).toString());
                }
                if (entity != null) {
                    entity.discard();
                }
            } catch (Exception e) {}
        }

        loadedMorphs = newMorphs;
        return newMorphs;
    }

    public static RegistryEntry.Reference<EntityType<?>> getLivingEntityType(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        RegistryEntry.Reference<EntityType<?>> reference = RegistryEntryReferenceArgumentType.getRegistryEntry(context, name, RegistryKeys.ENTITY_TYPE);
        if (!getAvailableMorphs().contains(Registries.ENTITY_TYPE.getId(reference.value()).toString())) {
            throw NOT_LIVING_EXCEPTION.create(reference.registryKey().getValue().toString());
        } else {
            return reference;
        }
    }
}