package org.cobra.api.cars.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.item.ModItems;
import org.cobra.api.cars.storage.CarFuelStorage;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BMW extends CarEntity implements GeoAnimatable {
    public static final Identifier CAR_ID = Identifier.of(CarsAPI.MOD_ID, "bmw");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BMW(EntityType<? extends CarEntity> car, World world) {
        super(car, world, 1);
        this.carType = CarType.SPORTS;
    }

//    public BMW(World world) {
//        this(ModEntities.BMW, world);
//    }

    @Override
    public String getModel() {
        return "bmw_m5";
    }

    @Override
    public Identifier getCarId() {
        return CAR_ID;
    }

    @Override
    protected Item asItem() {
        return ModItems.BMW_KEY;
    }

    @Override
    public CarFuelStorage createFuelStorage() {
        return new CarFuelStorage(1000L, 0L);
    }

    @Override
    public CarType getCarType() {
        return carType;
    }

    @Override
    public EngineType getEngineType() {
        return EngineType.V8;
    }

    @Override
    public float getFuelCapacity() {
        return this.getFuelStorage().getCapacity();
    }

    @Override
    public void drive(float distance) {
        if (this.getFuelTank() != null) {
            this.getFuelStorage().extract((long) (distance * getFuelEfficiency()));
        }
    }

    @Override
    public float getFuelEfficiency() {
        return 0.5f;
    }



    @Override
    public void tick() {
        CarFuelStorage storage = this.getFuelStorage();
        if(this.fuelStorage != null) {
            if (storage.getFuelAmount() == 0 || storage.getFuelAmount() <= 0) {
                this.refuel(5L);
            } else if (this.getVelocity() != null) {
                storage.extract((long) 2.5F);
            }
        }
        super.tick();

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(
                this, "controller", 0, state -> {
            return state.setAndContinue(RawAnimation.begin().then("animation.bmw.moving", Animation.LoopType.LOOP));
        }
        ));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return this.age;
    }
}
