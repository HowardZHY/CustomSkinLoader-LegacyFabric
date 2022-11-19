package customskinloader.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * MinecraftUtil for mcp version.
 * It is the only class in package 'customskinloader' which has differences in mcp/non-mcp version.
 * 
 * @author Alexander Xia
 * @since 13.6
 *
 */
public class MinecraftUtil {
	public static File getMinecraftDataDir0(){
		return net.minecraft.client.MinecraftClient.getInstance().runDirectory;
	}
	
	public static File minecraftDataFolder=null;
	public static File getMinecraftDataDir(){
		if(minecraftDataFolder!=null)
			return minecraftDataFolder;
		return new File("");
	}
	
	private static ArrayList<String> minecraftVersion=new ArrayList<String>();

	private static String minecraftMainVersion = "1.7.10";

	private final static Pattern MINECRAFT_VERSION_PATTERN = Pattern.compile(".*?(\\d+\\.\\d+[\\.]?\\d*).*?");

	public static ArrayList<String> getMinecraftVersions(){
		return minecraftVersion;
	}
	public static String getMinecraftVersionText(){
		StringBuilder sb=new StringBuilder();
		for(String version:getMinecraftVersions())
			sb.append(version).append(" ");
		return StringUtils.trim(sb.toString());
	}
	public static String getMinecraftMainVersion(){
		return minecraftMainVersion;
	}
	
	// (domain|ip)(:port)
	public static String getServerAddress(){
		net.minecraft.client.network.ServerInfo data = net.minecraft.client.MinecraftClient.getInstance().getCurrentServerEntry();
		if(data==null)//Single Player
			return null;
		return data.address;
	}
	// ip:port

	public static String getStandardServerAddress(){
		return HttpUtil0.parseAddress(getServerAddress());
	}

	public static boolean isLanServer(){
		return HttpUtil0.isLanServer(getStandardServerAddress());
	}

	public static String getCurrentUsername(){
		return net.minecraft.client.MinecraftClient.getInstance().getSession().getProfile().getName();
	}
	
	private final static Pattern MINECRAFT_CORE_FILE_PATTERN = Pattern.compile("^(.*?)/versions/([^\\/\\\\]*?)/([^\\/\\\\]*?).jar$");

	public static boolean isCoreFile(URL url){
        return false;
	}
	
	private final static Pattern LIBRARY_FILE_PATTERN = Pattern.compile("^(.*?)/libraries/(.*?)/([^\\/\\\\]*?).jar$");

	public static boolean isLibraryFile(URL url){
		return false;
	}
}
