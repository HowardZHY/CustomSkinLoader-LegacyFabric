package net.minecraft.client.renderer.texture;

import java.io.File;

import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

public class ThreadDownloadImageData extends ResourceTexture {
    public ThreadDownloadImageData(File cacheFileIn, String imageUrlIn, Identifier textureResourceLocation, BufferedImageSkinProvider imageBufferIn) {
        super(textureResourceLocation);
    }
}
