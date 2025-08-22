package org.cobra.api.cars.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;

public class ModScreenHandlerType {

    public static final ScreenHandlerType<CarScreenHandler> CAR = register("car", CarScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(CarsAPI.MOD_ID, id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static void register() {

    }

}
