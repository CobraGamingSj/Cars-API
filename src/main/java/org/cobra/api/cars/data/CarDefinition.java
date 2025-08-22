package org.cobra.api.cars.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.cobra.api.cars.storage.CarFuelStorage;
import org.cobra.api.cars.util.Car;

public record CarDefinition(CarFuelStorage.FuelTank.FuelType fuelType, float fuelCapacity, String model, Car.CarType carType) {

    public static final Codec<CarDefinition> CODEC = RecordCodecBuilder.create(carDefinitionInstance -> carDefinitionInstance.group(
                CarFuelStorage.FuelTank.FuelType.CODEC.fieldOf("fuelType").forGetter(CarDefinition::fuelType),
                Codec.FLOAT.fieldOf("fuelCapacity").forGetter(CarDefinition::fuelCapacity),
                Codec.STRING.fieldOf("model").forGetter(CarDefinition::model),
                Car.CarType.CODEC.fieldOf("carType").forGetter(CarDefinition::carType)
        ).apply(carDefinitionInstance, CarDefinition::new));

}
