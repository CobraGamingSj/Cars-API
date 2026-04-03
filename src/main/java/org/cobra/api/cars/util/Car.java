package org.cobra.api.cars.util;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.cobra.api.cars.screen.CarScreenHandler;
import org.cobra.api.cars.storage.CarFuelStorage;

public interface Car {

    CarFuelStorage.FuelTank getFuelTank();

    void drive(float distance);

    default Text getInDisplayName() {
        return Text.literal("Car");
    }

    float getFuelEfficiency();

    enum EngineType implements StringIdentifiable {
        V6, V8, V10, V12;

        public static final Codec<EngineType> CODEC = Codec.STRING.xmap(
                EngineType::valueOf,
                EngineType::name
        );

        @Override
        public String asString() {
            return this.name();
        }
    }

    enum CarType {
        NORMAL,
        SPORTS,
        LUXURY,
        SUPER;
        public static final Codec<CarType> CODEC = Codec.STRING.xmap(
                CarType::valueOf,
                CarType::name
        );
    }
}
