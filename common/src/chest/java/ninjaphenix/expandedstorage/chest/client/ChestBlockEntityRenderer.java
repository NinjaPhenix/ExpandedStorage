package ninjaphenix.expandedstorage.chest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import ninjaphenix.expandedstorage.base.internal_api.Utils;
import ninjaphenix.expandedstorage.base.internal_api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.chest.block.ChestBlock;
import ninjaphenix.expandedstorage.chest.block.misc.ChestBlockEntity;
import ninjaphenix.expandedstorage.chest.internal_api.ChestApi;

import java.util.Map;

public final class ChestBlockEntityRenderer extends BlockEntityRenderer<ChestBlockEntity> {
    private static final BlockState DEFAULT_STATE = Registry.BLOCK.get(Utils.resloc("wood_chest")).defaultBlockState();

    private static final Map<CursedChestType, SingleChestModel> MODELS = Utils.unmodifiableMap(map -> {
        map.put(CursedChestType.SINGLE, new SingleChestModel());
        map.put(CursedChestType.FRONT, new FrontChestModel());
        map.put(CursedChestType.BACK, new BackChestModel());
        map.put(CursedChestType.TOP, new TopChestModel());
        map.put(CursedChestType.BOTTOM, new BottomChestModel());
        map.put(CursedChestType.LEFT, new LeftChestModel());
        map.put(CursedChestType.RIGHT, new RightChestModel());
    });
    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction> LID_OPENNESS_FUNCTION_GETTER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction>() {
        @Override
        public Float2FloatFunction acceptDouble(ChestBlockEntity first, ChestBlockEntity second) {
            return (delta) -> Math.max(first.getLidOpenness(delta), second.getLidOpenness(delta));
        }

        @Override
        public Float2FloatFunction acceptSingle(ChestBlockEntity single) {
            return single::getLidOpenness;
        }

        @Override
        public Float2FloatFunction acceptNone() { // Should be an illegal case, but we'll provide a fallback just in case.
            return (delta) -> delta;
        }
    };

    public ChestBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(ChestBlockEntity entity, float delta, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        ResourceLocation blockId = entity.getBlockId();
        BlockState state = entity.hasLevel() ? entity.getBlockState() :
                ChestBlockEntityRenderer.DEFAULT_STATE.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        if (blockId == null || !(state.getBlock() instanceof ChestBlock block)) {
            return;
        }
        CursedChestType chestType = state.getValue(AbstractChestBlock.CURSED_CHEST_TYPE);
        SingleChestModel model = ChestBlockEntityRenderer.MODELS.get(chestType);
        stack.pushPose();
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        stack.translate(-0.5D, -0.5D, -0.5D);
        DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> compoundPropertyAccessor = entity.hasLevel() ?
                block.combine(state, entity.getLevel(), entity.getBlockPos(), true) :
                DoubleBlockCombiner.Combiner::acceptNone;
        VertexConsumer consumer = new Material(Sheets.CHEST_SHEET, ChestApi.INSTANCE.getChestTexture(blockId, chestType)).buffer(source, RenderType::entityCutout);
        float lidOpenness = compoundPropertyAccessor.apply(ChestBlockEntityRenderer.LID_OPENNESS_FUNCTION_GETTER).get(delta);
        int brightness = compoundPropertyAccessor.apply(new BrightnessCombiner<>()).applyAsInt(light);
        model.setLidPitch(lidOpenness);
        model.render(stack, consumer, brightness, overlay);
        stack.popPose();
    }
}
