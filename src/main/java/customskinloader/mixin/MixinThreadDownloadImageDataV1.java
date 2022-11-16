package customskinloader.mixin;

import java.awt.image.BufferedImage;

import customskinloader.fake.itf.IFakeThreadDownloadImageData;
import net.minecraft.client.texture.PlayerSkinTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerSkinTexture.class) // This mixin is only for 1.12.2-
public abstract class MixinThreadDownloadImageDataV1 implements IFakeThreadDownloadImageData {
    @Shadow
    private BufferedImage field_6550;

    @Shadow
    private boolean field_6553;

    @Override
    public void resetNewBufferedImage(BufferedImage image) {
        this.field_6553 = false;
        this.field_6550 = image;
    }
}
