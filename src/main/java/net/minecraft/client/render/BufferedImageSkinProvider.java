package net.minecraft.client.render;

import java.awt.image.BufferedImage;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.NativeImage;

public interface BufferedImageSkinProvider extends Runnable {
    BufferedImage parseSkin(BufferedImage image);

    NativeImage func_195786_a(NativeImage image);

    void setAvailable();

    @Override
    default void run() {
        this.setAvailable();
    }
}
