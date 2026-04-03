package org.cobra.api.cars.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;

public class ModEntities {

    private static final RegistryKey<EntityType<?>> BMW_REGISTRY_KEY =
            RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(CarsAPI.MOD_ID, "bmw"));

    public static final EntityType<BMW> BMW =
            Registry.register(Registries.ENTITY_TYPE, Identifier.of(CarsAPI.MOD_ID, "bmw"),
                    EntityType.Builder.create(BMW::new, SpawnGroup.MISC).dimensions(5F, 5F).build(BMW_REGISTRY_KEY));

    public static void register() {

    }
}
