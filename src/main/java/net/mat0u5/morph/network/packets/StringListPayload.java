package net.mat0u5.morph.network.packets;


import net.mat0u5.morph.Main;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

public record StringListPayload(String name, List<String> value) implements CustomPayload {

    public static final Id<StringListPayload> ID = new Id<>(Identifier.of(Main.MOD_ID, "stringlist"));
    public static final PacketCodec<RegistryByteBuf, StringListPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, StringListPayload::name,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), StringListPayload::value,
            StringListPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}