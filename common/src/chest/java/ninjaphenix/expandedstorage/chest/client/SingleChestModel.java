package ninjaphenix.expandedstorage.chest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;

public class SingleChestModel extends Model {
    protected final ModelPart lid;
    protected final ModelPart base;

    public SingleChestModel(int textureWidth, int textureHeight) {
        super(RenderType::entityCutout);
        texWidth = textureWidth;
        texHeight = textureHeight;
        lid = new ModelPart(this, 0, 0);
        base = new ModelPart(this, 0, 19);
    }

    public SingleChestModel() {
        this(64, 48);
        lid.addBox(0, 0, 0, 14, 5, 14, 0);
        lid.addBox(6, -2, 14, 2, 4, 1, 0);
        lid.setPos(1, 9, 1);
        base.addBox(0, 0, 0, 14, 10, 14, 0);
        base.setPos(1, 0, 1);
    }

    public final void setLidPitch(float pitch) {
        float p = 1.0f - pitch;
        lid.xRot = -((1.0F - p * p * p) * 1.5707964F);
    }

    public final void render(PoseStack stack, VertexConsumer consumer, int light, int overlay) {
        this.renderToBuffer(stack, consumer, light, overlay, 1, 1, 1, 1);
    }

    @Override
    public final void renderToBuffer(PoseStack stack, VertexConsumer consumer, int light, int overlay, float r, float g, float b, float f) {
        base.render(stack, consumer, light, overlay);
        lid.render(stack, consumer, light, overlay);
    }
}
