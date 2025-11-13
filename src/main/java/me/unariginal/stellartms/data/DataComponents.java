package me.unariginal.stellartms.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.cobblemon.mod.common.api.moves.Moves;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import me.unariginal.stellartms.StellarTMs;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public abstract class DataComponents {
    private static final String DEFAULT_MOVE = "blank";

    private static boolean isValidMove(String s) {
        if (s == null || s.isBlank()) return false;
        return s.equalsIgnoreCase(DEFAULT_MOVE) || Moves.getByName(s) != null;
    }

    private static final Codec<String> MOVE_CODEC = Codec.STRING.flatXmap(
            s -> DataResult.success(isValidMove(s) ? s.toLowerCase() : DEFAULT_MOVE),
            DataResult::success
    );

    private static final PacketCodec<RegistryByteBuf, String> MOVE_PACKET_CODEC = new PacketCodec<>() {
        @Override public String decode(RegistryByteBuf buf) {
            String s = PacketCodecs.STRING.decode(buf);
            return isValidMove(s) ? s.toLowerCase() : DEFAULT_MOVE;
        }
        @Override public void encode(RegistryByteBuf buf, String val) {
            PacketCodecs.STRING.encode(buf, (val == null || val.isBlank()) ? DEFAULT_MOVE : val.toLowerCase());
        }
    };

    public static final ComponentType<String> MOVE =
            register(builder -> builder.codec(MOVE_CODEC).packetCodec(MOVE_PACKET_CODEC));

    private static <T> ComponentType<T> register(UnaryOperator<ComponentType.Builder<T>> op) {
        ComponentType<T> component = Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(StellarTMs.MOD_ID, "move"),
                op.apply(ComponentType.builder()).build()
        );
        PolymerComponent.registerDataComponent(component);
        return component;
    }
}
