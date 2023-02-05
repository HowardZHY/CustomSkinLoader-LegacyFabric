package customskinloader.fabric;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import customskinloader.log.LogManager;
import customskinloader.log.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    public static Logger logger = LogManager.getLogger("Fabric");
    private long world_version;
    private long protocol_version;

    @Override
    public void onLoad(String mixinPackage) {
        LogManager.setLogFile(Paths.get("./CustomSkinLoader/CustomSkinLoader.log"));
        logger.warning("Your start is earlier than 18w47b.");
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
