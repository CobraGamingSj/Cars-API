package org.cobra.api.cars.client.entity.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.cobra.api.cars.client.entity.model.BMWCarEntityModel;
import org.cobra.api.cars.entity.BMWCarEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class BMWCarEntityRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<BMWCarEntity, R> {
    public BMWCarEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BMWCarEntityModel());
    }
}
