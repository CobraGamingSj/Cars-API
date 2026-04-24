package org.cobra.api.cars.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.cobra.api.cars.client.entity.render.BMWCarEntityRenderer;
import org.cobra.api.cars.entity.ModEntities;
import org.cobra.api.cars.screen.CarScreen;
import org.cobra.api.cars.screen.ModScreenHandlerType;

public class ModClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.BMW, BMWCarEntityRenderer::new);
        HandledScreens.register(ModScreenHandlerType.CAR, CarScreen::new);
    }
}
