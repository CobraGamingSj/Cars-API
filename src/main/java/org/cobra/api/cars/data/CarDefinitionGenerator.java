package org.cobra.api.cars.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.entity.ModEntities;
import org.cobra.api.cars.storage.CarFuelStorage;
import org.cobra.api.cars.util.Car;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CarDefinitionGenerator extends CarDefinitionDataGenerator.Provider {

    public CarDefinitionGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected CarDefinitionDataGenerator generateCarDefinition(RegistryWrapper.WrapperLookup registries, BiConsumer<Identifier, CarDefinition> exporter) {
        return new CarDefinitionDataGenerator(registries, exporter) {
            @Override
            public void generate() {
                exporter.accept(Identifier.of(CarsAPI.MOD_ID, "audi"), new CarDefinition(CarFuelStorage.FuelTank.FuelType.HYDROGEN, 60.0F, "audi_r8", Car.CarType.SPORTS));
                registerCarDefinition(ModEntities.BMW, new CarDefinition(CarFuelStorage.FuelTank.FuelType.PETROL, 5.0F, "bmw_m5", Car.CarType.SPORTS), exporter);
            }
        };
    }
}
