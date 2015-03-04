package Legacy;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configs{
	
	// other configs: contentLoc, ramDisk, ramDiskLoc
	private static Properties config = new Properties();
	
	public Configs() {
		try{
			config.load(new FileInputStream("config.cfg"));
		}catch(IOException io){
			System.out.println("config.cfg - Not Found!");
			io.printStackTrace();
			//Defaults options in Dove.
		}
	}
	
	public  String getProperty(String key){
		return config.getProperty(key);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args ){
		Properties config = new Properties();
		try{
			config.setProperty("mountLoc", "/media/Dove");
			config.setProperty("grepExcludes", " | grep -v -e '/dev/sda' -e '/dev/sdb'");
			config.store(new FileOutputStream("device.cfg"), null);
		}catch(IOException io){
			io.printStackTrace();
		}
	}
	
	public static boolean setConfig(String loc, String grepEx){
		Properties config = new Properties();
		try{
			config.setProperty("mountLoc", "/media/Dove");
			config.setProperty("grepExcludes", " | grep -v -e '/dev/sda' -e '/dev/sdb'");
			config.store(new FileOutputStream("device.cfg"), null);
			return true;
		}catch(IOException io){
			io.printStackTrace();
			return false;
		}
	}
	
/*	public static String getMountLoc(){
		Properties config = new Properties();
		String out ="";
		try{
			config.load(new FileInputStream("config.cfg"));
			out = config.getProperty("mountLoc");
			//System.out.println(out);
		}catch(IOException io){
			io.printStackTrace();
		}
		return out;
	}
	
	public static String getGrepExcludes(){
		Properties config = new Properties();
		String out ="";
		try{
			config.load(new FileInputStream("config.cfg"));
			out = config.getProperty("grepExcludes");
			//System.out.println(out);
		}catch(IOException io){
			io.printStackTrace();
		}
		return out;
	}
*/
	public String getConfig(String key){
		return config.getProperty(key);
	}
	public String toString(){
		return config.toString();
	}
}
