package ninjaphenix.expandedstorage.chest;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import ninjaphenix.expandedstorage.base.internal_api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.chest.internal_api.ChestApi;

import java.util.HashMap;
import java.util.Map;

public final class ChestImpl implements ChestApi {
    private static ChestImpl instance;
    private final Map<ResourceLocation, TextureCollection> textures = new HashMap<>();

    private ChestImpl() {

    }

    public static ChestApi getInstance() {
        if (instance == null) {
            instance = new ChestImpl();
        }
        return instance;
    }

    @Override
    public void declareChestTextures(ResourceLocation block, ResourceLocation singleTexture, ResourceLocation leftTexture, ResourceLocation rightTexture, ResourceLocation topTexture, ResourceLocation bottomTexture, ResourceLocation frontTexture, ResourceLocation backTexture) {
        if (!textures.containsKey(block)) {
            TextureCollection collection = new TextureCollection(singleTexture, leftTexture, rightTexture, topTexture, bottomTexture, frontTexture, backTexture);
            textures.put(block, collection);
        } else {
            throw new IllegalArgumentException("Tried registering chest textures for \"" + block + "\" which already has textures.");
        }

    }

    @Override
    public ResourceLocation getChestTexture(ResourceLocation block, CursedChestType chestType) {
        if (textures.containsKey(block)) {
            return textures.get(block).getTexture(chestType);
        } else {
            return MissingTextureAtlasSprite.getLocation();
        }
    }
}
