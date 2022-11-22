package customskinloader.fake.itf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class FakeInterfaceManager {
    public static InputStream IResource_getInputStream(Object resource) {
        return ((IFakeIResource.V2) resource).open();
    }

    public static Optional IResourceManager_getResource(Object resourceManager, Identifier location) throws IOException {
        return ((IFakeIResourceManager) resourceManager).getResource(location);
    }

    public static ResourceManager Minecraft_getResourceManager(MinecraftClient minecraft) {
        return ((IFakeMinecraft) minecraft).getResourceManager();
    }

    public static void Minecraft_addScheduledTask(MinecraftClient minecraft, Runnable runnable) {
        ((IFakeMinecraft) minecraft).execute(runnable);
    }

    public static void TextureManager_loadTexture(TextureManager textureManager, Identifier textureLocation, Object textureObj) {
        ((IFakeTextureManager.V2) textureManager).loadTexture(textureLocation, (Texture) textureObj);
    }

    public static Texture TextureManager_getTexture(TextureManager textureManager, Identifier textureLocation, Object textureObj) {
        return ((IFakeTextureManager.V1) textureManager).getTexture(textureLocation, (Texture) textureObj);
    }

    public static void ThreadDownloadImageData_resetNewBufferedImage(Object threadDownloadImageData, BufferedImage image) {
        ((IFakeThreadDownloadImageData) threadDownloadImageData).resetNewBufferedImage(image);
    }
}
