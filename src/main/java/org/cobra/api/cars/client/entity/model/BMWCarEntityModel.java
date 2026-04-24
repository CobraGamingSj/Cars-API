package org.cobra.api.cars.client.entity.model;

import net.minecraft.util.Identifier;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.entity.BMWCarEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BMWCarEntityModel extends GeoModel<BMWCarEntity> {
    @Override
    public Identifier getModelResource(GeoRenderState state) {
        return Identifier.of(CarsAPI.MOD_ID, "geo/bmw_m5.geo.json");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        return Identifier.of(CarsAPI.MOD_ID, "textures/entity/cars/bmw_m5.png");
    }

    @Override
    public Identifier getAnimationResource(BMWCarEntity entity) {
        return Identifier.of(CarsAPI.MOD_ID, "animations/bmw_m5.animation.json");
    }
}
