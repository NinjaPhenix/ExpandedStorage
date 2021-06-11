package ninjaphenix.expandedstorage.chest.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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

public final class ChestBlockEntityRenderer implements BlockEntityRenderer<ChestBlockEntity> {
    // todo: hopefully we can remove this mess once *hopefully* this is all json
    public static final ModelLayerLocation SINGLE_LAYER = new ModelLayerLocation(Utils.resloc("single_chest"), "main");
    public static final ModelLayerLocation VANILLA_LEFT_LAYER = new ModelLayerLocation(Utils.resloc("vanilla_left_chest"), "main");
    public static final ModelLayerLocation VANILLA_RIGHT_LAYER = new ModelLayerLocation(Utils.resloc("vanilla_right_chest"), "main");
    public static final ModelLayerLocation TALL_TOP_LAYER = new ModelLayerLocation(Utils.resloc("tall_top_chest"), "main");
    public static final ModelLayerLocation TALL_BOTTOM_LAYER = new ModelLayerLocation(Utils.resloc("tall_bottom_chest"), "main");
    public static final ModelLayerLocation LONG_FRONT_LAYER = new ModelLayerLocation(Utils.resloc("long_front_chest"), "main");
    public static final ModelLayerLocation LONG_BACK_LAYER = new ModelLayerLocation(Utils.resloc("long_back_chest"), "main");
    private static final BlockState DEFAULT_STATE = Registry.BLOCK.get(Utils.resloc("wood_chest")).defaultBlockState();
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
    private final ModelPart singleBottom, singleLid, singleLock;
    private final ModelPart vanillaLeftBottom, vanillaLeftLid, vanillaLeftLock;
    private final ModelPart vanillaRightBottom, vanillaRightLid, vanillaRightLock;
    private final ModelPart tallTopBottom, tallTopLid, tallTopLock;
    private final ModelPart tallBottomBottom;
    private final ModelPart longFrontBottom, longFrontLid, longFrontLock;
    private final ModelPart longBackBottom, longBackLid;

    public ChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart single = context.bakeLayer(SINGLE_LAYER);
        singleBottom = single.getChild("bottom");
        singleLid = single.getChild("lid");
        singleLock = single.getChild("lock");
        ModelPart vanillaLeft = context.bakeLayer(VANILLA_LEFT_LAYER);
        vanillaLeftBottom = vanillaLeft.getChild("bottom");
        vanillaLeftLid = vanillaLeft.getChild("lid");
        vanillaLeftLock = vanillaLeft.getChild("lock");
        ModelPart vanillaRight = context.bakeLayer(VANILLA_RIGHT_LAYER);
        vanillaRightBottom = vanillaRight.getChild("bottom");
        vanillaRightLid = vanillaRight.getChild("lid");
        vanillaRightLock = vanillaRight.getChild("lock");
        ModelPart tallTop = context.bakeLayer(TALL_TOP_LAYER);
        tallTopBottom = tallTop.getChild("bottom");
        tallTopLid = tallTop.getChild("lid");
        tallTopLock = tallTop.getChild("lock");
        ModelPart tallBottom = context.bakeLayer(TALL_BOTTOM_LAYER);
        tallBottomBottom = tallBottom.getChild("bottom");
        ModelPart longFront = context.bakeLayer(LONG_FRONT_LAYER);
        longFrontBottom = longFront.getChild("bottom");
        longFrontLid = longFront.getChild("lid");
        longFrontLock = longFront.getChild("lock");
        ModelPart longBack = context.bakeLayer(LONG_BACK_LAYER);
        longBackBottom = longBack.getChild("bottom");
        longBackLid = longBack.getChild("lid");
    }

    public static LayerDefinition createSingleBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1, 0, 1, 14, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 14, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7, -1, 15, 2, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createVanillaLeftBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1, 0, 1, 15, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 15, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(15, -1, 15, 1, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createVanillaRightBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0, 0, 1, 15, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0, 0, 0, 15, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0, -1, 15, 1, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createTallTopBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(1, 0, 1, 14, 10, 14), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 14, 5, 14), PartPose.offset(0, 9, 1));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7, -1, 15, 2, 4, 1), PartPose.offset(0, 8, 0));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createTallBottomBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 1, 14, 16, 14), PartPose.ZERO);
        return LayerDefinition.create(meshDefinition, 64, 32);
    }

    public static LayerDefinition createLongFrontBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 20).addBox(1, 0, 0, 14, 10, 15), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 15, 14, 5, 15), PartPose.offset(0, 9, -15));
        partDefinition.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7, -1, 31, 2, 4, 1), PartPose.offset(0, 8, -16));
        return LayerDefinition.create(meshDefinition, 64, 48);
    }

    public static LayerDefinition createLongBackBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 20).addBox(1, 0, 1, 14, 10, 15), PartPose.ZERO);
        partDefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(1, 0, 0, 14, 5, 15), PartPose.offset(0, 9, 1));
        return LayerDefinition.create(meshDefinition, 48, 48);
    }

    public static void registerModelLayers() {
        ModelLayers.ALL_MODELS.add(ChestBlockEntityRenderer.SINGLE_LAYER);
        ModelLayers.ALL_MODELS.add(ChestBlockEntityRenderer.VANILLA_LEFT_LAYER);
        ModelLayers.ALL_MODELS.add(ChestBlockEntityRenderer.VANILLA_RIGHT_LAYER);
        ModelLayers.ALL_MODELS.add(ChestBlockEntityRenderer.TALL_TOP_LAYER);
        ModelLayers.ALL_MODELS.add(ChestBlockEntityRenderer.TALL_BOTTOM_LAYER);
        ModelLayers.ALL_MODELS.add(ChestBlockEntityRenderer.LONG_FRONT_LAYER);
        ModelLayers.ALL_MODELS.add(ChestBlockEntityRenderer.LONG_BACK_LAYER);
    }

    public static void registerModelLayersDefinitions(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        builder.put(ChestBlockEntityRenderer.SINGLE_LAYER, createSingleBodyLayer());
        builder.put(ChestBlockEntityRenderer.VANILLA_LEFT_LAYER, createVanillaLeftBodyLayer());
        builder.put(ChestBlockEntityRenderer.VANILLA_RIGHT_LAYER, createVanillaRightBodyLayer());
        builder.put(ChestBlockEntityRenderer.TALL_TOP_LAYER, createTallTopBodyLayer());
        builder.put(ChestBlockEntityRenderer.TALL_BOTTOM_LAYER, createTallBottomBodyLayer());
        builder.put(ChestBlockEntityRenderer.LONG_FRONT_LAYER, createLongFrontBodyLayer());
        builder.put(ChestBlockEntityRenderer.LONG_BACK_LAYER, createLongBackBodyLayer());
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
        if (chestType == CursedChestType.SINGLE) {
            this.render(stack, consumer, singleBottom, singleLid, singleLock, lidOpenness, brightness, overlay);
        } else if (chestType == CursedChestType.TOP) {
            this.render(stack, consumer, tallTopBottom, tallTopLid, tallTopLock, lidOpenness, brightness, overlay);
        } else if (chestType == CursedChestType.BOTTOM) {
            this.render(stack, consumer, tallBottomBottom, null, null, lidOpenness, brightness, overlay);
        } else if (chestType == CursedChestType.FRONT) {
            this.render(stack, consumer, longFrontBottom, longFrontLid, longFrontLock, lidOpenness, brightness, overlay);
        } else if (chestType == CursedChestType.BACK) {
            this.render(stack, consumer, longBackBottom, longBackLid, null, lidOpenness, brightness, overlay);
        } else if (chestType == CursedChestType.LEFT) {
            this.render(stack, consumer, vanillaLeftBottom, vanillaLeftLid, vanillaLeftLock, lidOpenness, brightness, overlay);
        } else if (chestType == CursedChestType.RIGHT) {
            this.render(stack, consumer, vanillaRightBottom, vanillaRightLid, vanillaRightLock, lidOpenness, brightness, overlay);
        }
        stack.popPose();
    }

    private void render(PoseStack stack, VertexConsumer consumer, ModelPart bottom, ModelPart lid,
                        ModelPart lock, float openNess, int brightness, int overlay) {
        if (lid != null) {
            openNess = 1 - openNess;
            openNess = 1 - openNess * openNess * openNess;
            lid.xRot = -openNess * Mth.HALF_PI;
            lid.render(stack, consumer, brightness, overlay);
            if (lock != null) {
                lock.xRot = lid.xRot;
                lock.render(stack, consumer, brightness, overlay);
            }
        }
        bottom.render(stack, consumer, brightness, overlay);
    }
}
