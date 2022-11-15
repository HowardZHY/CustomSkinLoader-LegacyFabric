package net.minecraft.client.resources;

import java.io.IOException;

import net.minecraft.util.Identifier;

// this class shouldn't be created by ASM because of modlauncher issue.
public interface IResourceManager {
    IResource getResource(Identifier location) throws IOException;
}
