package customskinloader.fake.itf;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;

public interface IFakeMinecraft {
    // 1.13.2+
    default ResourceManager getResourceManager() {
        return (ResourceManager) ((MinecraftClient) this).getResourceManager();
    }
    // 1.14+
    default void execute(Runnable runnable) {
        ((MinecraftClient) this).method_6635(runnable);
    }
}
