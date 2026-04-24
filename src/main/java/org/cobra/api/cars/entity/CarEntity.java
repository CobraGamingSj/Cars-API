package org.cobra.api.cars.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleEntity;
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
import net.minecraft.util.math.BlockPos;
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

import java.util.List;

public abstract class CarEntity extends VehicleEntity implements Car, NamedScreenHandlerFactory {
    public boolean isLocked = true;
    public int keyId;
    private static final Logger LOGGER = LoggerFactory.getLogger(CarsAPI.MOD_ID);
    protected float distance;
    public CarFuelStorage fuelStorage;
    protected boolean initialized = false;
    protected CarFuelStorage.FuelTank fuelTank;
    protected CarType carType;
    protected String model;
    protected EngineType engineType;
    public static final TrackedData<Float> FUEL_AMOUNT = DataTracker.registerData(CarEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public static final TrackedData<Integer> KEY_ID = DataTracker.registerData(CarEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final DefaultedList<ItemStack> main = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public CarEntity(EntityType<? extends VehicleEntity> entityType, World world, int keyId) {
        super(entityType, world);
        this.keyId = keyId;
        this.fuelStorage = createFuelStorage();
    }

    public boolean isLocked() {
        return isLocked;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FUEL_AMOUNT, 0F);
        builder.add(KEY_ID, 0);
    }

    public abstract String getModel();
    public abstract Identifier getCarId();
    public abstract CarType getCarType();
    public abstract EngineType getEngineType();

    protected abstract float getFuelCapacity();

    public float getFuelAmount() {
        return this.getFuelStorage().getFuelAmount();
    }

    public CarFuelStorage getFuelStorage() {
        return fuelStorage;
    }

    public void refuel(float amount) {
        this.getFuelStorage().insert(amount);
    }

    public void setFuelStorage(CarFuelStorage fuelStorage) {
        this.fuelStorage = fuelStorage;
    }

    public DefaultedList<ItemStack> getMainStack() {
        return main;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        keyId = nbt.getInt("CarKeyID", 0);
        String engineTypeStr = nbt.getString("EngineType", "");
        if(engineTypeStr != null && !engineTypeStr.isEmpty()) {
            try {
                engineType = EngineType.valueOf(engineTypeStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                CarsAPI.LOGGER.warn("Invalid EngineType: {} defaulting to V6", engineTypeStr);
                engineType = EngineType.V6;
            }
        } else {
            CarsAPI.LOGGER.warn("Missing EngineType, defaulting to V6");
            engineType = EngineType.V6;
        }
        float capacity = nbt.getFloat("FuelCapacity", 0f);
        float amount = nbt.getFloat("FuelAmount", 0f);
        String fuelTypeStr = nbt.getString("FuelType", "");
        CarFuelStorage.FuelTank.FuelType fuelType;
        try {
            fuelType = CarFuelStorage.FuelTank.FuelType.valueOf(fuelTypeStr.toUpperCase()); // optional: force uppercase
        } catch (IllegalArgumentException | NullPointerException e) {
            CarsAPI.LOGGER.warn("Invalid FuelType '{}', defaulting to PETROL", fuelTypeStr);
            fuelType = CarFuelStorage.FuelTank.FuelType.PETROL;
        }

        this.fuelTank = new CarFuelStorage.FuelTank(capacity, fuelType);
        this.getFuelStorage().setFuelAmount(amount);

        if (nbt.contains("CarModel")) {
            model = nbt.getString("CarModel", "");
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if(fuelTank != null) {
            nbt.putFloat("FuelCapacity", fuelStorage.getCapacity());
            nbt.putFloat("FuelAmount", fuelStorage.getFuelAmount());
            if (fuelTank.getFuelType() != null) {
                nbt.putString("FuelType", fuelTank.getFuelType().name());
            }
        }

        nbt.putInt("CarKeyID", keyId);
        nbt.putString("EngineType", engineType.name());

        if (model != null) {
            nbt.putString("CarModel", model);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            CarsAPI.LOGGER.info("Cancelled interaction");
            return ActionResult.PASS;
        }

            ItemStack held = player.getStackInHand(hand);
            if (held.getItem() instanceof CarKeyItem key) {
                if(key.getKeyId() != this.keyId) {
                    CarsAPI.LOGGER.warn("Mismatched KeyID for car: {}", this.getCarId());
                }
                if (this.isLocked()) {
                    this.isLocked = false;
                    player.sendMessage(Text.of("Car unlocked!"), false);
                    return ActionResult.SUCCESS;
                } else if (player.isSneaking()) {
                    this.isLocked = true;
                    CarsAPI.LOGGER.info("Opening screen...");
                    // Open screen
                    player.openHandledScreen(this);
                    CarsAPI.LOGGER.info("Opened CarScreen");
                    return ActionResult.SUCCESS;
                }
            }

            if (!this.isLocked() && !hasPassenger(player) && (this.getWorld().isClient || player.startRiding(this))) {
                if(!this.getWorld().isClient) {
                    // Mount the player
                    player.startRiding(this);
                    CarsAPI.LOGGER.info("Player mounted");
                    return ActionResult.SUCCESS;
                }
            }
        CarsAPI.LOGGER.info("interact client");
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

    public CarFuelStorage.FuelTank getFuelTank() {
        return fuelTank;
    }

    public float getTrackedFuelAmount() {
        return this.getDataTracker().get(FUEL_AMOUNT);
    }

    public void setFuelAmount(float fuelAmount) {
         if(this.fuelTank != null) {
             this.getFuelStorage().setFuelAmount(fuelAmount);
             this.getDataTracker().set(FUEL_AMOUNT, fuelAmount);
        }
    }

    public float getTrackedKeyId() {
        return this.getDataTracker().get(KEY_ID);
    }

    public void setKeyId(int keyId) {
       this.getDataTracker().set(KEY_ID, keyId);

    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return source.isOf(DamageTypes.EXPLOSION);
    }

    protected void initialized() {
        CarDefinition def = CarDataLoader.CAR_DEFINITIONS.get(this.getCarId());
        CarDefinition.CarInfo info = def.carInfo();
        List<CarFuelStorage.FuelTank.FuelType> fuelTypes = def.fuelInfo().fuelType();
        CarFuelStorage.FuelTank.FuelType primary = fuelTypes.isEmpty() ? CarFuelStorage.FuelTank.FuelType.PETROL : fuelTypes.get(0);
        if (def != null) {
            this.fuelTank = new CarFuelStorage.FuelTank(def.fuelInfo().fuelCapacity(), primary);
            this.fuelStorage = createFuelStorage();
            this.carType = info.carType();
            this.engineType = info.engineType();
            this.model = info.model();
            this.keyId = info.keyId();
            this.dataTracker.set(KEY_ID, keyId);

            if(!info.model().equals(this.getModel())) {
                throw new IllegalStateException("Mismatched model in CarDefinition for Car ID " + this.getCarId() + " -> expected: " + this.getModel() + "," + " found: " + info.model());
            }

            CarsAPI.LOGGER.info("[{}] Loaded CarDefinition -> Fuel: {}, FuelType: {}, Model: {}, CarType: {}, EngineTye: {}, CarKeyID: {}",
                    this.getCarId(), this.fuelTank.getFuelCapacity(), this.fuelTank.getFuelType(), this.getModel(), this.getCarType(), this.getEngineType(), this.keyId);

        } else {
            CarsAPI.LOGGER.warn("Car config not found (ID: {})", this.getCarId());
        }
    }

    public abstract CarFuelStorage createFuelStorage();

    @Override
    public void tick() {
        if(!initialized) {
            if(this.getWorld().isClient) {
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


        if(this.hasPassengers() && hasEnoughFuel()) {
            this.drive(distance);
            Vec3d forward = this.getRotationVector().multiply(0.1);
            this.move(MovementType.SELF, forward);
            this.dataTracker.set(FUEL_AMOUNT, fuelTank.getFuelAmount());
        } else {
            this.setVelocity(this.getVelocity().multiply(0.85));
        }

        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.9));
    }

    public void initPosition(double x, double y, double z) {
        this.setPosition(x, y, z);
        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
    }

//    @Nullable
//    public static <T extends CarEntity> T create(World world, double x, double y, double z, EntityType<T> type, SpawnReason reason, ItemStack stack, @Nullable PlayerEntity player) {
//        T carEntity = (T)(type.create(world, reason));
//        if (carEntity != null) {
//            carEntity.initPosition(x, y, z);
//            EntityType.copier(world, stack, player).accept(carEntity);
//                BlockPos blockPos = carEntity.getBlockPos();
//                BlockState blockState = world.getBlockState(blockPos);
//
//        }
//
//        return carEntity;
//    }

    protected boolean hasEnoughFuel() {
        return fuelTank != null && fuelTank.getFuelAmount() != 0;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return (LivingEntity) this.getFirstPassenger() instanceof PlayerEntity playerEntity ? playerEntity : super.getControllingPassenger();
    }
}
