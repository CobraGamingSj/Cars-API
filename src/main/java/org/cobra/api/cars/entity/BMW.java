package org.cobra.api.cars.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.storage.CarFuelStorage;
import org.cobra.api.cars.storage.GasStorage;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BMW extends CarEntity<Long, GasStorage> implements GeoAnimatable {
    public static final Identifier CAR_ID = Identifier.of(CarsAPI.MOD_ID, "bmw");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BMW(EntityType<? extends CarEntity> entityType, World world) {
        super(entityType, world);
        this.carType = CarType.SPORTS;
    }

    @Override
    public String getModel() {
        return "bmw_m5";
    }

    @Override
    public Identifier getCarId() {
        return CAR_ID;
    }

    @Override
    protected Long fromFloat(float amount) {
        return (long) amount;
    }

    @Override
    public GasStorage createFuelStorage() {
        return new GasStorage(1000L, 0L);
    }

    @Override
    public Long getFuelAmount() {
        return this.getFuelStorage().getFuelAmount();
    }

    @Override
    public CarType getCarType() {
        return carType;
    }

    @Override
    public Long getFuelCapacity() {
        return this.getFuelStorage().getCapacity();
    }

    @Override
    public void drive(Long distance) {
        if (this.getFuelTank() != null) {
            this.getFuelStorage().extract((long) (distance * getFuelEfficiency()));
        }
    }

    @Override
    public Long getFuelEfficiency() {
        return (long) 0.5f;
    }



    @Override
    public void tick() {
        CarFuelStorage<Long> storage = this.getFuelStorage();
        if(this.fuelStorage != null) {
            if (storage.getFuelAmount() == null || storage.getFuelAmount() <= 0) {
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
