package org.cobra.api.cars.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.data.CarDataLoader;
import org.cobra.api.cars.data.CarDefinition;
import org.cobra.api.cars.item.CarKeyItem;
import org.cobra.api.cars.screen.CarScreenHandler;
import org.cobra.api.cars.storage.CarFuelStorage;
import org.cobra.api.cars.util.Car;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CarEntity<T extends Number, S extends CarFuelStorage<T>> extends Entity implements Car<T>, NamedScreenHandlerFactory {
    public boolean isLocked = true;
    public final int carId;
    private static final Logger LOGGER = LoggerFactory.getLogger(CarsAPI.MOD_ID);
    protected T distance;
    public S fuelStorage;
    protected boolean initialized = false;
    protected CarFuelStorage.FuelTank<T> fuelTank;
    protected CarType carType;
    protected String model;
    private static final TrackedData<Float> FUEL_AMOUNT = DataTracker.registerData(CarEntity.class, TrackedDataHandlerRegistry.FLOAT);

    private static final DefaultedList<ItemStack> main = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public CarEntity(EntityType<?> entityType, World world, int carId) {
        super(entityType, world);
        this.carId = carId;
        this.fuelStorage = createFuelStorage();
    }

    public int getCarID() {
        return this.carId;
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(FUEL_AMOUNT, 0F);
    }

    public abstract String getModel();
    public abstract Identifier getCarId();
    public abstract CarType getCarType();

    protected abstract T getFuelCapacity();

    public T getFuelAmount() {
        return this.getFuelStorage().getFuelAmount();
    }

    public S getFuelStorage() {
        return fuelStorage;
    }

    public void refuel(T amount) {
        this.getFuelStorage().insert(amount);
    }

    public void setFuelStorage(S fuelStorage) {
        this.fuelStorage = fuelStorage;
    }

    public DefaultedList<ItemStack> getMainStack() {
        return main;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        float capacity = nbt.getFloat("FuelCapacity");
        float amount = nbt.getFloat("FuelAmount");
        String fuelTypeStr = nbt.getString("FuelType");
        CarFuelStorage.FuelTank.FuelType fuelType;
        try {
            fuelType = CarFuelStorage.FuelTank.FuelType.valueOf(fuelTypeStr.toUpperCase()); // optional: force uppercase
        } catch (IllegalArgumentException | NullPointerException e) {
            CarsAPI.LOGGER.warn("Invalid FuelType '{}', defaulting to PETROL", fuelTypeStr);
            fuelType = CarFuelStorage.FuelTank.FuelType.PETROL;
        }

        this.fuelTank = new CarFuelStorage.FuelTank<>(fromFloat(capacity), fuelType);
        this.getFuelStorage().setFuelAmount(fromFloat(amount));

        if (nbt.contains("CarModel")) {
            model = nbt.getString("CarModel");
        }
    }

    protected abstract T fromFloat(float amount);

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if(fuelTank != null) {
            nbt.putFloat("FuelCapacity", fuelStorage.getCapacity().floatValue());
            nbt.putFloat("FuelAmount", fuelStorage.getFuelAmount().floatValue());
            if (fuelTank.getFuelType() != null) {
                nbt.putString("FuelType", fuelTank.getFuelType().name());
            }
        }

        if (model != null) {
            nbt.putString("CarModel", model);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient) {
            if (player.shouldCancelInteraction()) {
                return ActionResult.PASS;
            }

            ItemStack held = player.getStackInHand(hand);
            if(held.getItem() instanceof CarKeyItem key && key.getCar() == this) {
                if(this.isLocked) {
                    this.isLocked = false;
                    player.sendMessage(Text.of("Car unlocked!"), false);
                    return ActionResult.SUCCESS;
                } else if(player.isSneaking()) {
                    this.isLocked = true;
                }
            }

            if (player.isSneaking()) {
                System.out.println("Opening screen...");
                // Open screen
                player.openHandledScreen(this); // Make sure CarEntity implements `NamedScreenHandlerFactory`
                System.out.println("Opened CarScreen");
                return ActionResult.SUCCESS;
            } else if(!this.isLocked() && !hasPassenger(player)) {
                System.out.println("Player mounted!");
                // Mount the player
                player.startRiding(this);
                System.out.println("Player mounted");
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasNoGravity() {
        return false;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CarScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return this.getInDisplayName();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof PlayerEntity;
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        super.updatePassengerPosition(passenger, positionUpdater);
        if(passenger instanceof PlayerEntity player) {
            double x = this.getX();
            double y = this.getY() + 1.0;
            double z = this.getZ();
            player.setPosition(x, y, z);
        }
    }

    public CarFuelStorage.FuelTank<T> getFuelTank() {
        return fuelTank;
    }

    public float getTrackedFuelAmount() {
        return this.getDataTracker().get(FUEL_AMOUNT);
    }

    public void setFuelAmount(T fuelAmount) {
         if(this.fuelTank != null) {
             this.getFuelStorage().setFuelAmount(fuelAmount);
             this.getDataTracker().set(FUEL_AMOUNT, fuelAmount.floatValue());
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return source.isOf(DamageTypes.EXPLOSION);
    }

    protected void initialized() {
        CarDefinition def = CarDataLoader.CAR_DEFINITIONS.get(this.getCarId());
        if (def != null) {
            this.fuelTank = new CarFuelStorage.FuelTank(def.fuelCapacity(), def.fuelType());
            this.fuelStorage = createFuelStorage();
            this.carType = def.carType();
            this.model = def.model();

            if(!def.model().equals(this.getModel())) {
                throw new IllegalStateException("Mismatched model in CarDefinition for Car ID " + this.getCarId() + " -> expected: " + this.getModel() + "," + " found: " + def.model());
            }

            CarsAPI.LOGGER.info("[{}] Loaded CarDefinition -> Fuel: {}, FuelType: {}, Model: {}, CarType: {}",
                    this.getCarId(), this.fuelTank.getFuelCapacity(), this.fuelTank.getFuelType(), this.getModel(), this.getCarType());

        } else {
            CarsAPI.LOGGER.warn("Car config not found (ID: {})", this.getCarId());
        }
    }

    public abstract S createFuelStorage();

    @Override
    public void tick() {
        super.tick();

        if(!initialized) {
            if(!this.getWorld().isClient) {
                initialized();
                initialized = true;
                LOGGER.info("Car initialized on server");
            }
            return;
        } else {
            LOGGER.debug("Car already loaded");
        }

        if(this.getFuelStorage() == null) {
            CarsAPI.LOGGER.error("FuelStorage is null for Car: {}", this.getCarId());
            return;
        }

        if(this.getWorld().isClient) return;

        if(this.hasPassengers() && hasEnoughFuel()) {
            this.drive(distance);
            Vec3d forward = this.getRotationVector().multiply(0.1);
            this.move(MovementType.SELF, forward);
            this.dataTracker.set(FUEL_AMOUNT, fuelTank.getFuelAmount().floatValue());
        } else {
            this.setVelocity(this.getVelocity().multiply(0.85));
        }

        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.9));
    }

    protected boolean hasEnoughFuel() {
        return fuelTank != null && fuelTank.getFuelAmount() != null;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return (LivingEntity) this.getFirstPassenger() instanceof PlayerEntity playerEntity ? playerEntity : super.getControllingPassenger();
    }

    public void unlock() {
        if(!isLocked) {
            this.isLocked = false;
        }
    }
}
