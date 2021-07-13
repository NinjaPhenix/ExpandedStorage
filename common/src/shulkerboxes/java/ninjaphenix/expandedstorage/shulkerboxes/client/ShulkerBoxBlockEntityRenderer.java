package ninjaphenix.expandedstorage.shulkerboxes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import ninjaphenix.expandedstorage.shulkerboxes.block.misc.ShulkerBoxBlockEntity;

public class ShulkerBoxBlockEntityRenderer extends BlockEntityRenderer<ShulkerBoxBlockEntity> {
    public ShulkerBoxBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ShulkerBoxBlockEntity entity, float delta, PoseStack stack, MultiBufferSource source, int light, int overlay) {

    }
}
