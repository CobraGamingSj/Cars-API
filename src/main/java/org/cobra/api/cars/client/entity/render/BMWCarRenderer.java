package org.cobra.api.cars.client.entity.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import org.cobra.api.cars.client.entity.model.BMWModel;
import org.cobra.api.cars.entity.BMW;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BMWCarRenderer extends GeoEntityRenderer<BMW> {
    public BMWCarRenderer(EntityRendererFactory.Context context) {
        super(context, new BMWModel());
    }
}
