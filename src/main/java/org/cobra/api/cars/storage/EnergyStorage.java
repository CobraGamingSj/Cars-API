package org.cobra.api.cars.storage;

public class EnergyStorage extends CarFuelStorage<Integer> {
    protected EnergyStorage(Integer capacity, Integer initialAmount) {
        super(capacity, initialAmount);
    }

    @Override
    public void insert(Integer maxAmount) {
        int amount = Math.min(this.capacity - this.fuelAmount, maxAmount);
        this.fuelAmount += amount;
    }

    @Override
    public void extract(Integer maxAmount) {
        int amount = Math.min(this.fuelAmount, maxAmount);
        this.fuelAmount -= amount;
    }
}
