package org.cobra.api.cars.client.entity.model;

import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.entity.BMW;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class BMWModel extends GeoModel<BMW> {
    @Override
    public Identifier getModelResource(BMW entity, @Nullable GeoRenderer<BMW> geoRenderer) {
        return Identifier.of(CarsAPI.MOD_ID, "geo/bmw_m5.geo.json");
    }

    @Override
    public Identifier getTextureResource(BMW entity, @Nullable GeoRenderer<BMW> geoRenderer) {
        return Identifier.of(CarsAPI.MOD_ID, "textures/entity/cars/" + entity.getModel() + ".png");
    }

    @Override
    public Identifier getAnimationResource(BMW entity) {
        return Identifier.of(CarsAPI.MOD_ID, "animations/bmw_m5.animation.json");
    }
}
