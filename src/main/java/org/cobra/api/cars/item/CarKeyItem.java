package org.cobra.api.cars.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.cobra.api.cars.entity.CarEntity;

public class CarKeyItem extends Item {
    private final CarEntity<?, ?> car;
    private final int carId;

    public CarKeyItem(Settings settings, CarEntity<?, ?> car, int carId) {
        super(settings);
        this.car = car;
        this.carId = car.getCarID();
    }

    public int getCarId() {
        return carId;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(!entity.getWorld().isClient && (Entity) entity instanceof CarEntity<?,?> car) {
                if(car.isLocked()){
                    car.unlock();
                    return ActionResult.SUCCESS;
                }
            }
        return ActionResult.PASS;
    }

    public CarEntity<?, ?> getCar() {
        return car;
    }
}
