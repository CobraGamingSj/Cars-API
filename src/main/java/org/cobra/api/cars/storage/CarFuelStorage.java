package org.cobra.api.cars.storage;

import com.mojang.serialization.Codec;

public abstract class CarFuelStorage<T extends Number> {

    protected T fuelAmount;
    protected final T capacity;

    protected CarFuelStorage(T capacity, T initialAmount) {
        this.capacity = capacity;
        this.fuelAmount = initialAmount;
    }

    public T getFuelAmount() {
        return this.fuelAmount;
    }

    public void setFuelAmount(T amount) {
        this.fuelAmount = amount;
    }

    public T getCapacity() {
        return this.capacity;
    }

    public abstract void insert(T maxAmount);
    public abstract void extract(T maxAmount);

    public static class FuelTank<T extends Number> {

        private T capacity;
        private T fuelAmount;
        private FuelType fuelType;

        public FuelTank(T capacity, FuelType fuelType) {
            this.capacity = capacity;
            this.fuelType = fuelType;
        }

        public T getFuelCapacity() {
            return capacity;
        }

        public void setFuelCapacity(T capacity) {
            this.capacity = capacity;
        }

        public FuelType getFuelType() {
            return fuelType;
        }

        public void setFuelType(FuelType fuelType) {
            this.fuelType = fuelType;
        }

        public T getFuelAmount() {
            return fuelAmount;
        }

        public enum FuelType {
            PETROL,
            ELECTRIC,
            HYDROGEN;

            public static final Codec<FuelType> CODEC = Codec.STRING.xmap(
                    FuelType::valueOf,
                    FuelType::name
            );
        }
    }
}
