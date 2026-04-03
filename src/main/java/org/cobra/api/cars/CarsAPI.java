package org.cobra.api.cars;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.cobra.api.cars.component.CarsAPIDataComponentTypes;
import org.cobra.api.cars.data.CarDataLoader;
import org.cobra.api.cars.entity.ModEntities;
import org.cobra.api.cars.item.ModItems;
import org.cobra.api.cars.screen.ModScreenHandlerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarsAPI implements ModInitializer {
	public static final String MOD_ID = "cars-api";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModItems.register();
        ModEntities.register();
        CarsAPIDataComponentTypes.register();
		ModScreenHandlerType.register();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CarDataLoader());
	}
}