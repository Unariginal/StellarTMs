package me.unariginal.stellartms.data;

import com.mojang.serialization.Codec;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import me.unariginal.stellartms.StellarTMs;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.UnaryOperator;

public abstract class DataComponents {
    public static final ComponentType<String> MOVE = register("move", builder -> builder.codec(Codec.STRING));

    private static <T> ComponentType<T> register (String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        ComponentType<T> component = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(StellarTMs.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
        PolymerComponent.registerDataComponent(component);
        return component;
    }
}
