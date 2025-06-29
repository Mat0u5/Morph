package net.mat0u5.morph.morph;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
                Entity entity = entityType.create(server.getOverworld());
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

    public static void getBaseDimensions(PlayerEntity player, EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (morphComponent.isMorphed()) {
            float scaleRatio = 1 / player.getScale();
            LivingEntity dummy = morphComponent.getDummy();
            if (morphComponent.isMorphed() && dummy != null){
                cir.setReturnValue(dummy.getDimensions(pose).scaled(scaleRatio, scaleRatio));
            }
        }
    }

    public static float modifyEntityScale(Entity focusedEntity, float originalDistance) {
        if (!(focusedEntity instanceof PlayerEntity player)) return originalDistance;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if (morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if (dummy != null) {
                float playerHeight = PlayerEntity.STANDING_DIMENSIONS.height();
                float morphedHeight = dummy.getDimensions(EntityPose.STANDING).height();
                float heightScale = morphedHeight / playerHeight;
                return heightScale * 4.0F;
            }
        }
        return originalDistance;
    }

    //? if <= 1.21 {
    public static void replaceRendering(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if (abstractClientPlayerEntity.isSpectator() || abstractClientPlayerEntity.isInvisible()) return;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(abstractClientPlayerEntity);
        if (morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null){
                MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                        dummy, 0, 0, 0, f, g, matrixStack, vertexConsumerProvider, i);
                ci.cancel();
            }
        }
    }
    //?} else {
    /*public static <E extends Entity> void render(Entity entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity player)) return;
        if (player.isSpectator() || player.isInvisible()) return;
        MorphComponent morphComponent = MorphManager.getOrCreateComponent(player);
        if(morphComponent.isMorphed()) {
            LivingEntity dummy = morphComponent.getDummy();
            if(morphComponent.isMorphed() && dummy != null){
                MinecraftClient.getInstance().getEntityRenderDispatcher().render(
                        dummy, x, y, z, tickDelta, matrices, vertexConsumers, light);
                ci.cancel();
            }
        }
    }
    *///?}
}