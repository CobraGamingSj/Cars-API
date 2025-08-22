package org.cobra.api.cars.util;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.cobra.api.cars.screen.CarScreenHandler;
import org.cobra.api.cars.storage.CarFuelStorage;

public interface Car<T extends Number> {

    CarFuelStorage.FuelTank<T> getFuelTank();

    void drive(T distance);

    default Text getInDisplayName() {
        return Text.literal("Car");
    }

    T getFuelEfficiency();

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
