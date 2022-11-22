package customskinloader.profile;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import org.apache.commons.lang3.StringUtils;

/**
 * Model Manager for 1.8 and higher.
 * A manager to check if model is available.
 * It is the only class in package 'customskinloader' which has differences in different Minecraft version.
 *
 * @since 13.1
 */
public class ModelManager0 {
    public enum Model {
        SKIN_DEFAULT,
        SKIN_SLIM,
        CAPE,
        ELYTRA
    }

    private static HashMap<String, Model> models = new HashMap<String, Model>();
    private static Type typeElytra = null;

    static {
        for (Type type : Type.values()) {
            if (type.ordinal() == 2)//ELYTRA
                typeElytra = type;
        }
        models.put("default", Model.SKIN_DEFAULT);
        models.put("slim", Model.SKIN_SLIM);
        models.put("cape", Model.CAPE);
        if (typeElytra != null) {
            models.put("elytra", Model.ELYTRA);
            models.put("elytron", Model.ELYTRA);
        }
    }

    /**
     * Get enum for the model.
     *
     * @param model - string model
     * @since 14.5
     */
    public static Model getEnumModel(String model) {
        return models.get(model);
    }

    /**
     * Check if model is skin.
     *
     * @since 14.5
     */
    public static boolean isSkin(Model model) {
        return model == Model.SKIN_DEFAULT || model == Model.SKIN_SLIM;
    }

    /**
     * Check if elytra is supported.
     *
     * @since 14.5
     */
    public static boolean isElytraSupported() {
        return typeElytra != null;
    }

    /**
     * Parse hashMapProfile to UserProfile
     *
     * @param profile - hashMapProfile (HashMap<String,MinecraftProfileTexture>)
     * @return profile - UserProfile instance
     * @since 13.1
     */
    public static UserProfile toUserProfile(Map<Type, MinecraftProfileTexture> profile) {
        UserProfile userProfile = new UserProfile();
        if (profile == null)
            return userProfile;
        MinecraftProfileTexture skin = profile.get(Type.SKIN);
        userProfile.skinUrl = skin == null ? null : skin.getUrl();//Avoid NullPointerException
        userProfile.model = skin == null ? null : skin.getMetadata("model");
        if (StringUtils.isEmpty(userProfile.model))
            userProfile.model = "default";

        MinecraftProfileTexture cape = profile.get(Type.CAPE);
        userProfile.capeUrl = cape == null ? null : cape.getUrl();
        return userProfile;
    }

    /**
     * Parse UserProfile to hashMapProfile
     *
     * @param profile - UserProfile instance
     * @return profile - hashMapProfile (HashMap<String,MinecraftProfileTexture>)
     * @since 13.1
     */
    public static Map<Type, MinecraftProfileTexture> fromUserProfile(UserProfile profile) {
        Map<Type, MinecraftProfileTexture> map = Maps.newHashMap();
        if (profile == null)
            return map;
        if (profile.skinUrl != null) {
            Map<String, String> metadata = null;
            if ("slim".equals(profile.model) || "auto".equals(profile.model)) {
                metadata = Maps.newHashMap();
                metadata.put("model", profile.model);
            }
            map.put(Type.SKIN, getProfileTexture(profile.skinUrl, metadata));
        }
        if (profile.capeUrl != null)
            map.put(Type.CAPE, getProfileTexture(profile.capeUrl, null));
        if (typeElytra != null && profile.elytraUrl != null)
            map.put(typeElytra, getProfileTexture(profile.elytraUrl, null));
        return map;
    }

    /**
     * Parse url to MinecraftProfileTexture
     * Fix authlib 21 bug for 1.7.10
     * @param url - textureUrl
     *        metadata - metadata
     * @return MinecraftProfileTexture
     * @since 14.5
     */
    public static MinecraftProfileTexture getProfileTexture(String url,Map metadata){
        Map hashMap=new HashMap<String,String>();
        hashMap.put("url", url);
        Gson gson=new Gson();
        String json=gson.toJson(hashMap);
        MinecraftProfileTexture pt=gson.fromJson(json, MinecraftProfileTexture.class);
        return pt;
    }
}
