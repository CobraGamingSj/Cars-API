package org.cobra.api.cars.screen;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import org.cobra.api.cars.entity.CarEntity;
import org.cobra.api.cars.entity.ModEntities;
import org.cobra.api.cars.storage.CarFuelStorage;

public class CarScreenHandler extends ScreenHandler {
    protected CarEntity<?, ?> carEntity;
    private final Inventory inventory;

    public CarScreenHandler(int syncId , PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    public CarScreenHandler(int syncId , PlayerInventory playerInventory, CarEntity<?, ?> entityType) {
        super(ModScreenHandlerType.CAR, syncId);
        checkSize((Inventory) playerInventory, 2);
        this.inventory = (Inventory) playerInventory;
        this.carEntity = (CarEntity<?, ?>) entityType;

        addPlayerGenericInventory(playerInventory);
        addPlayerHotbarInventory(playerInventory);

        this.addSlot(new Slot(inventory, 0, 77, 58));
    }

    public CarEntity<?, ?> getCarEntity() {
        return this.carEntity;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public void addPlayerGenericInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 115 + i * 18));
            }
        }
    }

    public void addPlayerHotbarInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 173));
        }
    }

    public Number getFuelPercentage() {
        CarFuelStorage<?> storage = this.carEntity.getFuelStorage();
        Number amount = storage.getFuelAmount();
        Number capacity = storage.getCapacity();
        return MathHelper.clamp((float) amount / (float) capacity, 0, 1);
    }
}
