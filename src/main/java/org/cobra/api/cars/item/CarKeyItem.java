package org.cobra.api.cars.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.cobra.api.cars.component.CarsAPIDataComponentTypes;
import org.cobra.api.cars.entity.CarEntity;

import java.util.List;
import java.util.function.Consumer;

public class CarKeyItem extends Item {
    private final int keyId;
    private final EntityType<? extends CarEntity> car;

    public CarKeyItem(Settings settings, int keyId, EntityType<? extends CarEntity> car) {
        super(settings);
        this.keyId = keyId;
        this.car = car;
    }

    public int getKeyId() {
        return keyId;
    }

//    @Override
//    public ActionResult useOnBlock(ItemUsageContext context) {
//        World world = context.getWorld();
//        if(!(world instanceof ServerWorld serverWorld)) {
//            return ActionResult.SUCCESS;  // or PASS on client
//        }
//
//        CarEntity car = this.car.create(world, SpawnReason.NATURAL);
//        if(car == null) return ActionResult.FAIL;
//
//        car.setPosition(context.getBlockPos().toCenterPos());
//        serverWorld.spawnEntity(car);
//
//        return ActionResult.SUCCESS;
//    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        stack.set(CarsAPIDataComponentTypes.CAR_KEY_ID, keyId);
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        if(stack.get(CarsAPIDataComponentTypes.CAR_KEY_ID) != null) {
            textConsumer.accept(Text.literal("Car Key ID: " + CarsAPIDataComponentTypes.CAR_KEY_ID));
        }
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
    }

}
