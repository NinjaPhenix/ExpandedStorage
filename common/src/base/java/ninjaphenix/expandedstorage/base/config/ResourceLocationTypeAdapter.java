package ninjaphenix.expandedstorage.base.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public final class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter writer, ResourceLocation value) throws IOException {
        writer.value(value.toString());
    }

    @Override // never used.
    public ResourceLocation read(JsonReader reader) {
        return null;
    }
}
