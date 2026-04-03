package org.cobra.api.cars.data;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.cobra.api.cars.storage.CarFuelStorage;
import org.cobra.api.cars.util.Car;

import java.util.List;

public record CarDefinition(CarInfo carInfo, FuelInfo fuelInfo) {

    public static final Codec<CarDefinition> CODEC = RecordCodecBuilder.create(carDefinitionInstance -> carDefinitionInstance.group(
                CarInfo.CODEC.fieldOf("car").forGetter(CarDefinition::carInfo),
                FuelInfo.CODEC.fieldOf("fuel").forGetter(CarDefinition::fuelInfo)
        ).apply(carDefinitionInstance, CarDefinition::new));

    public record CarInfo(Car.CarType carType, Car.EngineType engineType, String model, int keyId) {
        public static final Codec<CarInfo> CODEC = RecordCodecBuilder.create(carInfoInstance -> carInfoInstance.group(
                Car.CarType.CODEC.fieldOf("type").forGetter(CarInfo::carType),
                Car.EngineType.CODEC.fieldOf("engineType").forGetter(CarInfo::engineType),
                Codec.STRING.fieldOf("model").forGetter(CarInfo::model),
                Codec.INT.fieldOf("keyId").forGetter(CarInfo::keyId)
        ).apply(carInfoInstance, CarInfo::new));
    }

    public record FuelInfo(List<CarFuelStorage.FuelTank.FuelType> fuelType, float fuelCapacity) {

        public static final Codec<List<CarFuelStorage.FuelTank.FuelType>> LISTED_CODEC = Codec.either(CarFuelStorage.FuelTank.FuelType.CODEC, CarFuelStorage.FuelTank.FuelType.CODEC.listOf())
                .xmap(either -> either.map(List::of, list -> list), list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list));
        
        public static final Codec<FuelInfo> CODEC = RecordCodecBuilder.create(fuelInfoInstance -> fuelInfoInstance.group(
                FuelInfo.LISTED_CODEC.fieldOf("type").forGetter(FuelInfo::fuelType),
                Codec.FLOAT.fieldOf("capacity").forGetter(FuelInfo::fuelCapacity)
        ).apply(fuelInfoInstance, FuelInfo::new));


    }
}
