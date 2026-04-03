package org.cobra.api.cars.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.cobra.api.cars.CarsAPI;
import org.cobra.api.cars.entity.CarEntity;
import software.bernie.geckolib.object.Color;

public class CarScreen extends HandledScreen<CarScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(CarsAPI.MOD_ID, "textures/gui/container/car.png");
    private final CarEntity entity = handler.carEntity;

    public CarScreen(CarScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    private void renderFuelHandler(DrawContext context, int x, int y) {
        int fuelHandlerSize = MathHelper.ceil((Float) this.handler.getFuelPercentage() * 80);
        context.fill(x + 10, y + 30 + 80 - fuelHandlerSize, x + 10 + 30, y + 30 + 80, -16777216);
    }

    private void renderEntity(CarEntity carEntity, int x, int y, float scale, float mouseX, float mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

        float yaw = 180.0F + (mouseX - x) * 0.5F;
        float pitch = (mouseY - y) * 0.5F;

        MatrixStack matrices = new MatrixStack();
        matrices.push();

        matrices.translate(x, y, 50);
        matrices.scale(scale, scale, scale);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(yaw));

        entity.setYaw(yaw);
        entity.setPitch(pitch);
        entity.prevYaw = yaw;
        entity.prevPitch = pitch;

        dispatcher.setRenderShadows(false);
        dispatcher.render(carEntity, 0.0, 0.0, 0.0, 0F, matrices, immediate, 15728880);
        dispatcher.setRenderShadows(true);
        matrices.pop();

        immediate.draw();
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext drawContext, int x, int y) {
        super.drawMouseoverTooltip(drawContext, x, y);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
        renderEntity(this.entity, i + 54, j + 18, 17F, mouseX, mouseY);
        renderFuelHandler(context, i, j);
    }
}
