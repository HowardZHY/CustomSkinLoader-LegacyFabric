package customskinloader.loader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import com.mojang.authlib.GameProfile;
import customskinloader.CustomSkinLoader;
import customskinloader.config.SkinSiteProfile;
import customskinloader.plugin.ICustomSkinLoaderPlugin;
import customskinloader.profile.UserProfile;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpUtil0;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonAPILoader implements ICustomSkinLoaderPlugin, ProfileLoader.IProfileLoader {

    public interface IJsonAPI {
        List<IDefaultProfile> getDefaultProfiles(JsonAPILoader loader);

        String toJsonUrl(String root, String username);

        default String getPayload(SkinSiteProfile ssp) {
            return null;
        }

        /**
         * get JSON API payload, which will make a POST request
         *
         * @param ssp      skin site profile
         * @param username username
         * @return JSON payload
         * @since 14.16
         */
        default String getPayload(SkinSiteProfile ssp, String username) {
            return getPayload(ssp);
        }

        UserProfile toUserProfile(String root, String json, boolean local);

        String getName();
    }

    public static class ErrorProfile {
        public int errno;
        public String msg;
    }

    private final IJsonAPI jsonAPI;

    public JsonAPILoader(IJsonAPI jsonAPI) {
        this.jsonAPI = jsonAPI;
    }

    // === ICustomSkinLoaderPlugin ===

    @Override
    public ProfileLoader.IProfileLoader getProfileLoader() {
        return this;
    }

    @Override
    public List<IDefaultProfile> getDefaultProfiles() {
        return this.jsonAPI.getDefaultProfiles(this);
    }

    public abstract static class DefaultProfile implements ICustomSkinLoaderPlugin.IDefaultProfile {
        protected final JsonAPILoader loader;

        public DefaultProfile(JsonAPILoader loader) {
            this.loader = loader;
        }

        @Override
        public void updateSkinSiteProfile(SkinSiteProfile ssp) {
            ssp.type = this.loader.getName();
            ssp.root = this.getRoot();
        }

        public abstract String getRoot();
    }

    // === IProfileLoader ===

    @Override
    public UserProfile loadProfile(SkinSiteProfile ssp, GameProfile gameProfile) throws Exception {
        String username = gameProfile.getName();
        if (StringUtils.isEmpty(ssp.root)) {
            CustomSkinLoader.logger.info("Root not defined.");
            return null;
        }
        boolean local = HttpUtil0.isLocal(ssp.root);
        String jsonUrl = this.jsonAPI.toJsonUrl(ssp.root, username);
        //Some API like MinecraftCapesAPI won't load profile at sometime
        if (jsonUrl == null) {
            CustomSkinLoader.logger.info("Profile url not found.");
            return null;
        }
        String json;
        if (local) {
            File jsonFile = new File(CustomSkinLoader.DATA_DIR, jsonUrl);
            if (!jsonFile.exists()) {
                CustomSkinLoader.logger.info("Profile File not found.");
                return null;
            }
            json = IOUtils.toString(Files.newInputStream(jsonFile.toPath()), StandardCharsets.UTF_8);
        } else {
            try {
                HttpRequestUtil.HttpResponce responce = HttpRequestUtil
                        .makeHttpRequest(new HttpRequestUtil.HttpRequest(jsonUrl).setCacheTime(90)
                                .setUserAgent(ssp.userAgent).setPayload(this.jsonAPI.getPayload(ssp, username)));
                json = responce.content;
            } catch (ProfileNotFoundException ignored) {
                CustomSkinLoader.logger.info("Profile not found.");
                return null;
            }
        }
        if (json == null || json.equals("")) {
            CustomSkinLoader.logger.info("Profile not found.");
            return null;
        }

        ErrorProfile profile = CustomSkinLoader.GSON.fromJson(json, ErrorProfile.class);
        if (profile.errno != 0) {
            CustomSkinLoader.logger.info("Error " + profile.errno + ": " + profile.msg);
            return null;
        }

        UserProfile p = this.jsonAPI.toUserProfile(ssp.root, json, local);
        if (p == null || p.isEmpty()) {
            CustomSkinLoader.logger.info("Both skin and cape not found.");
            return null;
        } else {
            return p;
        }
    }

    @Override
    public boolean compare(SkinSiteProfile ssp0, SkinSiteProfile ssp1) {
        return !StringUtils.isNoneEmpty(ssp0.root) || ssp0.root.equalsIgnoreCase(ssp1.root);
    }

    @Override
    public String getName() {
        return this.jsonAPI.getName();
    }

    @Override
    public void init(SkinSiteProfile ssp) {
        if (HttpUtil0.isLocal(ssp.root)) {
            File f = new File(ssp.root);
            if (!f.exists())
                //noinspection ResultOfMethodCallIgnored
                f.mkdirs();
        }
    }

    /**
     * When getting payload, this exception can end skin loading
     * @since 14.16
     */
    public static class ProfileNotFoundException extends RuntimeException {
    }
}
