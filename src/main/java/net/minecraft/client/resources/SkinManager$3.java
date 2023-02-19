package net.minecraft.client.resources;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.renderer.texture.TextureManager;

import java.io.File;

public class SkinManager$3 extends SkinManager implements Runnable{
    public SkinManager.SkinAvailableCallback field_152801_c;
    public SkinManager field_152802_d;

    public SkinManager$3(TextureManager textureManagerInstance, File skinCacheDirectory, MinecraftSessionService sessionservice) {
        super(textureManagerInstance, skinCacheDirectory, sessionservice);
    }

    public void run() {
    }
}