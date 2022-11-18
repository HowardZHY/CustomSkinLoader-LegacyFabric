package customskinloader.fake.itf;

import java.io.InputStream;

import net.minecraft.resource.Resource;

/** { net.minecraft(.client).resources.IResource} is no longer an interface since 22w14a */
public interface IFakeIResource {
    // 1.13.2 ~ 22w13a
    interface V1 {
        default InputStream getInputStream() {
            return ((Resource) this).getInputStream();
        }
    }

    // 22w14a+
    interface V2 {
        default InputStream open() {
            return ((IFakeIResource.V1) this).getInputStream();
        }
    }
}
