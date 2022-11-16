package net.minecraft.resources;

import java.io.InputStream;

// this class shouldn't be created by ASM because of modlauncher issue.
// exclude
public interface IResource {
    InputStream getInputStream();
}
