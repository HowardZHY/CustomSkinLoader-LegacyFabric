package customskinloader.profile;

import java.io.File;
import java.util.Deque;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.CustomSkinLoader;
import customskinloader.utils.TimeUtil;
import org.apache.commons.io.FileUtils;

public class ProfileCache {
    public static File PROFILE_CACHE_DIR=new File(CustomSkinLoader.DATA_DIR,"ProfileCache");
    
    private Map<String, CachedProfile> cachedProfiles = new ConcurrentHashMap<>();
    private Map<String, UserProfile> localProfiles = new ConcurrentHashMap<>();
    private Map<String, Deque<Consumer<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>>> profileLoaders = new ConcurrentHashMap<>();
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ProfileCache(){
        if(!PROFILE_CACHE_DIR.exists())
            PROFILE_CACHE_DIR.mkdir();
    }
    
    public boolean isExist(String username){
        return cachedProfiles.containsKey(username.toLowerCase());
    }
    public boolean isLoading(String username) {
        CachedProfile cp=cachedProfiles.get(username.toLowerCase());
        return cp != null && cp.loading;
    }
    public boolean isReady(String username){
        CachedProfile cp=cachedProfiles.get(username.toLowerCase());
        return cp != null && (cp.loading || cp.expiryTime > TimeUtil.getCurrentUnixTimestamp());
    }
    public boolean isExpired(String username){
        CachedProfile cp=cachedProfiles.get(username.toLowerCase());
        return cp == null || (cp.expiryTime <= TimeUtil.getCurrentUnixTimestamp());
    }
    
    public UserProfile getProfile(String username){
        return getCachedProfile(username).profile;
    }
    public long getExpiry(String username){
        return getCachedProfile(username).expiryTime;
    }
    public UserProfile getLocalProfile(String username){
        if(localProfiles.containsKey(username.toLowerCase()))
            return localProfiles.get(username.toLowerCase());
        return loadLocalProfile(username);
    }
    public Optional<Consumer<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>> getLastLoader(String username) {
        Deque<Consumer<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>> deque = this.profileLoaders.get(username);
        if (deque != null) {
            return Optional.ofNullable(deque.pollLast());
        }
        return Optional.empty();
    }
    
    public void setLoading(String username,boolean loading){
        getCachedProfile(username).loading=loading;
    }
    public void updateCache(String username,UserProfile profile){
        updateCache(username,profile,CustomSkinLoader.config.enableLocalProfileCache);
    }
    public void updateCache(String username,UserProfile profile,boolean saveLocalProfile){
        CachedProfile cp=getCachedProfile(username);
        cp.profile=profile;
        cp.expiryTime=TimeUtil.getUnixTimestamp(CustomSkinLoader.config.cacheExpiry);
        if(!saveLocalProfile)
            return;
        saveLocalProfile(username,profile);
    }
    public void putLoader(String username, Consumer<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> loader) {
        this.profileLoaders.putIfAbsent(username, new ConcurrentLinkedDeque<>());
        this.profileLoaders.get(username).offerLast(loader);
    }
    
    private CachedProfile getCachedProfile(String username){
        CachedProfile cp=cachedProfiles.get(username.toLowerCase());
        if(cp!=null)
            return cp;
        cp=new CachedProfile();
        cachedProfiles.put(username.toLowerCase(), cp);
        return cp;
    }
    private UserProfile loadLocalProfile(String username){
        File localProfile=new File(PROFILE_CACHE_DIR,username.toLowerCase()+".json");
        if(!localProfile.exists()){
            localProfiles.put(username.toLowerCase(), null);
        }
        try{
            String json = FileUtils.readFileToString(localProfile, "UTF-8");
            UserProfile profile=CustomSkinLoader.GSON.fromJson(json, UserProfile.class);
            localProfiles.put(username.toLowerCase(), profile);
            CustomSkinLoader.logger.info("Successfully load LocalProfile.");
            return profile;
        }catch(Exception e){
            CustomSkinLoader.logger.info("Failed to load LocalProfile.("+e.toString()+")");
            localProfiles.put(username.toLowerCase(), null);
        }
        return null;
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveLocalProfile(String username, UserProfile profile){
        String json=CustomSkinLoader.GSON.toJson(profile);
        File localProfile=new File(PROFILE_CACHE_DIR,username+".json");
        if(localProfile.exists())
            localProfile.delete();
        try {
            FileUtils.write(localProfile, json, "UTF-8");
            CustomSkinLoader.logger.info("Successfully save LocalProfile.");
        } catch (Exception e) {
            CustomSkinLoader.logger.info("Failed to save LocalProfile.("+e.toString()+")");
        }
    }
}
class CachedProfile{
    public UserProfile profile;
    public long expiryTime=0;
    public boolean loading=false;
}
