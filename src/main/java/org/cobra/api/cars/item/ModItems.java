package org.cobra.api.cars.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.cobra.api.cars.entity.ModEntities;

public class ModItems {

    public static final Item BMW_KEY = Items.register("bmw_key", settings -> new CarKeyItem(settings, 1, ModEntities.BMW));

    public static void register() {

    }

}
