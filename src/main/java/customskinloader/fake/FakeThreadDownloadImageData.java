package customskinloader.fake;

import java.io.File;
import java.util.EnumMap;
import java.util.function.Supplier;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.CustomSkinLoader;
import customskinloader.utils.HttpRequestUtil;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

public class FakeThreadDownloadImageData {
    private static IThreadDownloadImageDataBuilder builder;

    public static ResourceTexture createThreadDownloadImageData(File cacheFileIn, String imageUrlIn, Identifier textureResourceLocationIn, BufferedImageSkinProvider imageBufferIn, MinecraftProfileTexture.Type textureTypeIn) {
        ResourceTexture texture = null;
        if (FakeThreadDownloadImageData.builder == null) {
            EnumMap<ThreadDownloadImageDataBuilder, Throwable> throwables = new EnumMap<>(ThreadDownloadImageDataBuilder.class);
            for (ThreadDownloadImageDataBuilder builder : ThreadDownloadImageDataBuilder.values()) {
                try {
                    FakeThreadDownloadImageData.builder = builder.get().get();
                    texture = FakeThreadDownloadImageData.builder.build(cacheFileIn, imageUrlIn, textureResourceLocationIn, imageBufferIn, textureTypeIn);
                    CustomSkinLoader.logger.info("ThreadDownloadImageData Class: %s", texture.getClass().getName());
                    break;
                } catch (Throwable t) {
                    throwables.put(builder, t);
                }
            }

            if (texture == null) {
                CustomSkinLoader.logger.warning("Unable to get ThreadDownloadImageData Class: ");
                throwables.forEach((k, v) -> {
                    CustomSkinLoader.logger.warning("Caused by: (%s)", k.name());
                    CustomSkinLoader.logger.warning(v);
                });
                throw new RuntimeException("Unable to get ThreadDownloadImageData Class!");
            }
        } else {
            texture = builder.build(cacheFileIn, imageUrlIn, textureResourceLocationIn, imageBufferIn, textureTypeIn);
        }
        return texture;
    }

    public static void downloadTexture(File cacheFile, String imageUrl) {
        HttpRequestUtil.HttpRequest request = new HttpRequestUtil.HttpRequest(imageUrl).setLoadContent(false).setCacheTime(0).setCacheFile(cacheFile);
        for (int i = 0; i <= CustomSkinLoader.config.retryTime; i++) {
            if (i != 0) {
                CustomSkinLoader.logger.debug("Retry to download texture %s (%s)", imageUrl, i);
            }
            if (HttpRequestUtil.makeHttpRequest(request).success) {
                break;
            }
        }
    }
    private interface IThreadDownloadImageDataBuilder {
        ResourceTexture build(File cacheFile, String imageUrl, Identifier textureResourceLocationIn, BufferedImageSkinProvider imageBuffer, MinecraftProfileTexture.Type textureTypeIn);
    }

    private enum ThreadDownloadImageDataBuilder {
        // DO NOT replace new IThreadDownloadImageDataBuilder() with lambda.
        V1(() -> new IThreadDownloadImageDataBuilder() { // Forge 1.8.x~1.12.x
            @Override
            public ResourceTexture build(File cacheFile, String imageUrl, Identifier textureResourceLocation, BufferedImageSkinProvider imageBuffer, MinecraftProfileTexture.Type textureType) {
                return new PlayerSkinTexture(cacheFile, imageUrl, textureResourceLocation, imageBuffer);
            }
        });

        private final Supplier<IThreadDownloadImageDataBuilder> builder;

        ThreadDownloadImageDataBuilder(Supplier<IThreadDownloadImageDataBuilder> builder) {
            this.builder = builder;
        }

        public Supplier<IThreadDownloadImageDataBuilder> get() {
            return this.builder;
        }
    }
}
