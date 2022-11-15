package net.minecraft.client.texture;

import java.awt.image.BufferedImage;

import net.minecraft.client.renderer.texture.NativeImage;

public interface BufferedImageSkinProvider extends Runnable {
    BufferedImage parseUserSkin(BufferedImage image);

    NativeImage func_195786_a(NativeImage image);

    void skinAvailable();

    @Override
    default void run() {
        this.skinAvailable();
    }
}
