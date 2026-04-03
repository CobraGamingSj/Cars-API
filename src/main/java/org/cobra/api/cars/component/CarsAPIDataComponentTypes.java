package org.cobra.api.cars.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;

import java.util.function.UnaryOperator;

public class CarsAPIDataComponentTypes {

    public static final ComponentType<Integer> CAR_KEY_ID = register("car_key_id", integerComponentType -> integerComponentType.codec(Codec.INT));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builder) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(CarsAPI.MOD_ID, name), builder.apply(ComponentType.<T>builder()).build());
    }

    public static void register() {

    }

}
