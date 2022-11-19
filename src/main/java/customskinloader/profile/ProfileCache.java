package customskinloader.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;

import customskinloader.CustomSkinLoader;
import customskinloader.utils.TimeUtil;

public class ProfileCache {
	public static File PROFILE_CACHE_DIR=new File(CustomSkinLoader.DATA_DIR,"ProfileCache");
	
	private HashMap<String,CachedProfile> cachedProfiles=new HashMap<String,CachedProfile>();
	private HashMap<String,UserProfile> localProfiles=new HashMap<String,UserProfile>();
	
	public ProfileCache(){
		if(!PROFILE_CACHE_DIR.exists())
			PROFILE_CACHE_DIR.mkdir();
	}
	
	public boolean isExist(String username){
		return cachedProfiles.containsKey(username.toLowerCase());
	}
	public boolean isReady(String username){
		CachedProfile cp=cachedProfiles.get(username.toLowerCase());
		return cp==null?false:(cp.loading||cp.expiryTime>TimeUtil.getCurrentUnixTimestamp());
	}
	public boolean isExpired(String username){
		CachedProfile cp=cachedProfiles.get(username.toLowerCase());
		return cp==null?true:(cp.expiryTime<=TimeUtil.getCurrentUnixTimestamp());
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
	
	public void setLoading(String username,boolean loading){
		getCachedProfile(username).loading=loading;
	}
	public void updateCache(String username,UserProfile profile){
		updateCache(username,profile,CustomSkinLoader.config.enableLocalProfileCache);
	}
	public void updateCache(String username,UserProfile profile,boolean saveLocalProfile){
		CachedProfile cp=getCachedProfile(username);
		cp.profile=profile;
		cp.expiryTime=TimeUtil.getUnixTimestampRandomDelay(CustomSkinLoader.config.cacheExpiry);
		if(!saveLocalProfile)
			return;
		saveLocalProfile(username,profile);
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
			String json=IOUtils.toString(new FileInputStream(localProfile));
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
	private void saveLocalProfile(String username,UserProfile profile){
		String json=CustomSkinLoader.GSON.toJson(profile);
		File localProfile=new File(PROFILE_CACHE_DIR,username.toLowerCase()+".json");
		if(localProfile.exists())
			localProfile.delete();
		try {
			localProfile.createNewFile();
			IOUtils.write(json, new FileOutputStream(localProfile));
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