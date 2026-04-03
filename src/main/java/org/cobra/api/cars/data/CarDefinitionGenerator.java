package org.cobra.api.cars.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.entity.ModEntities;
import org.cobra.api.cars.storage.CarFuelStorage;
import org.cobra.api.cars.util.Car;

import java.util.List;
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
                exporter.accept(Identifier.of(CarsAPI.MOD_ID, "audi"), new CarDefinition(new CarDefinition.CarInfo(Car.CarType.SPORTS, Car.EngineType.V8, "audi_r8", 0), new CarDefinition.FuelInfo(List.of(CarFuelStorage.FuelTank.FuelType.HYDROGEN), 60.0F)));
                registerCarDefinition(ModEntities.BMW, new CarDefinition(new CarDefinition.CarInfo(Car.CarType.SPORTS, Car.EngineType.V8,"bmw_m5", 1), new CarDefinition.FuelInfo(List.of(CarFuelStorage.FuelTank.FuelType.PETROL, CarFuelStorage.FuelTank.FuelType.HYDROGEN), 5.0F)), exporter);
            }
        };
    }
}
