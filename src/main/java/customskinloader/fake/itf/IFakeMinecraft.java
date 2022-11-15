package customskinloader.fake.itf;

import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ThreadExecutor;
import org.apache.commons.lang3.Validate;
import java.util.concurrent.Executors;

public interface IFakeMinecraft {
    // 1.13.2+
    default IResourceManager func_195551_G() {
        return (IResourceManager) ((MinecraftClient) this).getResourceManager();
    }

    default void execute(Runnable runnable) {
        ((MinecraftClient) this).submit(runnable);
    }
}
