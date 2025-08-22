package org.cobra.api.cars.storage;

public  class GasStorage extends CarFuelStorage<Long> {
    public GasStorage(Long capacity, Long initialAmount) {
        super(capacity, initialAmount);
    }

    @Override
    public void insert(Long maxAmount) {
        long amount = Math.min(this.capacity - this.fuelAmount, maxAmount);
        this.fuelAmount += amount;
    }

    @Override
    public void extract(Long maxAmount) {
        long amount = Math.min(this.fuelAmount, maxAmount);
        this.fuelAmount -= amount;
    }
}
