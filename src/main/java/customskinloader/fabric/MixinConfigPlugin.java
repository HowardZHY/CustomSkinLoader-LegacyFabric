package customskinloader.fabric;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import customskinloader.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    public static Logger logger = new Logger(new File("./CustomSkinLoader/FabricPlugin.log"));

    private long world_version;
    private long protocol_version;

    @Override
    public void onLoad(String mixinPackage) {
        URL versionJson = this.getClass().getResource("/version.json");
        if (versionJson != null) {
            logger.info("\"version.json\": " + versionJson.toString());
            try (
                InputStream is = versionJson.openStream();
                InputStreamReader isr = new InputStreamReader(is)
            ) {
                JsonObject object = new JsonParser().parse(isr).getAsJsonObject();
                String name = object.get("name").getAsString();
                this.protocol_version = object.get("protocol_version").getAsLong();
                logger.info("MinecraftVersion: {name='" + name + "' + protocol_version='" + this.protocol_version + "'}");
            } catch (Throwable t) {
                logger.warning("An exception occurred when reading \"version.json\"!");
                logger.warning((Exception) t);
            }
        } else {
            logger.info("CSL is loading on LF.");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean result = true;
        logger.info("target: " + targetClassName + ", mixin: " + mixinClassName + ", result: " + result);
        return result;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    // To be compatible with 0.7.11
    public void preApply(String targetClassName, org.spongepowered.asm.lib.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    // To be compatible with 0.7.11
    public void postApply(String targetClassName, org.spongepowered.asm.lib.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
