package net.minecraft.client.texture;

import java.io.File;

import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;

public class DownloadingTexture extends ResourceTexture {
    public DownloadingTexture(File cacheFileIn, String imageUrlIn, Identifier textureResourceLocation, BufferedImageSkinProvider imageBufferIn) {
        super(textureResourceLocation);
    }

    public DownloadingTexture(File cacheFileIn, String imageUrlIn, Identifier textureResourceLocation, boolean legacySkinIn, Runnable processTaskIn) {
        super(textureResourceLocation);
    }
}
