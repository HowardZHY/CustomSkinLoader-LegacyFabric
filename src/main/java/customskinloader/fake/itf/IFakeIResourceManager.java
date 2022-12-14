package customskinloader.fake.itf;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

public interface IFakeIResourceManager {
    // 1.13.2 ~ 22w13a
    default Resource func_199002_a(Identifier location) throws IOException {
        return (Resource) ((ResourceManager) this).getResource(location);
    }

    // 22w14a+
    default Optional getResource(Identifier location) throws IOException {
        return Optional.ofNullable(this.func_199002_a(location));
    }
}
