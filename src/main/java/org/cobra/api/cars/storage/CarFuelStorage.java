package org.cobra.api.cars.storage;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public class CarFuelStorage {

    protected float fuelAmount;
    protected final float capacity;

    public CarFuelStorage(float capacity, float initialAmount) {
        this.capacity = capacity;
        this.fuelAmount = initialAmount;
    }

    public float getFuelAmount() {
        return this.fuelAmount;
    }

    public void setFuelAmount(float amount) {
        this.fuelAmount = amount;
    }

    public float getCapacity() {
        return this.capacity;
    }

    public void insert(float maxAmount) {
        float amount = Math.min(this.capacity - this.fuelAmount, maxAmount);
        this.fuelAmount += amount;
    }

    public void extract(float maxAmount) {
        float amount = Math.min(this.fuelAmount, maxAmount);
        this.fuelAmount -= amount;
    }

    public static class FuelTank {

        private float capacity;
        private float fuelAmount;
        private FuelType fuelType;

        public FuelTank(float capacity, FuelType fuelType) {
            this.capacity = capacity;
            this.fuelType = fuelType;
        }

        public float getFuelCapacity() {
            return capacity;
        }

        public void setFuelCapacity(float capacity) {
            this.capacity = capacity;
        }

        public FuelType getFuelType() {
            return fuelType;
        }

        public void setFuelType(FuelType fuelType) {
            this.fuelType = fuelType;
        }

        public float getFuelAmount() {
            return fuelAmount;
        }

        public enum FuelType implements StringIdentifiable {
            PETROL,
            ELECTRIC,
            HYDROGEN;

            public static final Codec<FuelType> CODEC = Codec.STRING.xmap(
                    FuelType::valueOf,
                    FuelType::name
            );

            @Override
            public String asString() {
                return this.name();
            }
        }
    }
}
