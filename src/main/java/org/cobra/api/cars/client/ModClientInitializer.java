package org.cobra.api.cars.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.cobra.api.cars.client.entity.render.BMWCarRenderer;
import org.cobra.api.cars.entity.ModEntities;
import org.cobra.api.cars.screen.CarScreen;
import org.cobra.api.cars.screen.ModScreenHandlerType;

public class ModClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.BMW, BMWCarRenderer::new);
        HandledScreens.register(ModScreenHandlerType.CAR, CarScreen::new);
    }
}
