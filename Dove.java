import java.io.*;
import java.nio.file.*;
import java.util.Properties;
//import java.util.ArrayList;

/**
 * Main class. Handles configuration, main copy function, and object links.
 * 
 */
public class Dove {
	private Content src;
	//private Drive  drv;
	private Devices devs;
	private Properties config = new Properties();
	private long bytesCopied = 0;
	private final String CONFIG_LOCATION = System.getProperty("user.home")
			+File.separator+".dove"+File.separator;
	//Default values
	private final String DEF_MOUNT_LOCATION = "/media/Dove";
	private final String DEF_GREP_EXCLUDES = "false";
	private final String DEF_CONTENT_LOCATN = "/home/";
	private final String DEF_FOLDER_NAME = "Dove";
	private final String DEF_ALLOW_NO_THUMB_CONTENT = "true";
	private final String DEF_SEARCH_FILE_NAMES = "false";
	/**
	 * Starts configuration from either config file or defaults listed below.
	 * example: mountLoc = config.getProperty("mountLocation", "/media/Dove");
	 *   pulls the data from the config file, but defaults to "/media/Dove"
	 *   if config file not found.
	 * 
	 */
	public Dove(){
		Boolean loaded = loadConfigs(); //loads configs, or defaults
		String mountLoc;
		String grepEx;
		String contentLoc;
		String folderName;
		String allowNoThumb;
		String searchFileNames;
		//Read from config files, or use hard-defaults below
		mountLoc = config.getProperty("mountLocation", DEF_MOUNT_LOCATION);
		grepEx = config.getProperty("grepExcludes", DEF_GREP_EXCLUDES);
		if(Boolean.parseBoolean(grepEx) == true){
			DriveSkipper ds = new DriveSkipper();
			grepEx = ds.getExcludeString();
		}else{
			grepEx = "";
		}
		contentLoc = config.getProperty("contentLocation", DEF_CONTENT_LOCATN);
		folderName = config.getProperty("folderName", DEF_FOLDER_NAME);
		allowNoThumb = config.getProperty("allowNoThumbContent", 
			DEF_ALLOW_NO_THUMB_CONTENT);
		searchFileNames = config.getProperty("searchFileNames", 
			DEF_SEARCH_FILE_NAMES);
		src = new Content(contentLoc, Boolean.parseBoolean(allowNoThumb));
		src.setSearchFileNames(Boolean.parseBoolean(searchFileNames));
		devs = new Devices(mountLoc, grepEx, folderName);
		//System.out.println("[Dove] "+Configs.toString());
	}
	/**
	 * Calls config into Properties object
	 */
	private boolean loadConfigs(){
		try{
			config.load(new FileInputStream(configLocation +File.separator +
					"config.cfg"));
			System.out.println("[Dove] Config file successfully loaded: "+
					config.toString()
					+"\n"+configLocation);
			return true;
		}catch(IOException io){
			System.out.println("[Dove] config.cfg - Not Found!"+configLocation);
			//io.printStackTrace();
			//TODO use defualt options?? or call config wizard
			
			return false;
		}
	}
	public boolean preCopy(int i){
		// TODO Checks everything to make sure that copy will go smoothly; 
		//implement or delete
		//should be called before copy()
		//returns boolean ALL CLEAR!!
		//boolean sizeOkay, nameOkay,
		return true;
	}
	
	public boolean copy(int index)throws IOException{ 
		//make new Folder on drive
		//loop that copies all entries in folder
		File load = src.getFileAt(index);
		return copyBase(load);
	}
	
	public boolean copy(ContentItem item)throws IOException{ 
		//make new Folder on drive
		//loop that copies all entries in folder
		File load = item.getFile();
		return copyBase(load);
	}
	private boolean copyBase(File load) throws IOException{
		//  check for folder name on drive and rename if already there
		File check = new File(devs.getMountedDrive().getDoveFile()
				.getAbsolutePath() + File.separator + load.getName());
		int i=1;
		while (check.exists()){
			//Folder is already there, pick new folder name 
			check = new File(devs.getMountedDrive().getDoveFile()
					.getAbsolutePath() + File.separator + 
					load.getName() + "("+ i++ +")");
		}
		check.mkdir();
		String temp = check.toString();
		bytesCopied = load.length(); //set to folderSize before copying
		System.out.println("Folder size: " + bytesCopied);
		dive(load, temp);
		return true;
	}
	
	private void dive(File f, String bc) throws IOException{
		File files[] = f.listFiles();
		for(int i=0;i<files.length;i++){
			File target = new File(bc + File.separator + 
					files[i].getName());
			target.setWritable(true);
			Files.copy(files[i].toPath(), target.toPath() );
			bytesCopied += files[i].length();
			System.out.print(files[i].toPath() + " : " );
			System.out.println(new File(bc + File.separator + 
					files[i].getName()).toPath() +" "+ files[i].length() );
			if(files[i].isDirectory()){
				dive(files[i], bc + File.separator + files[i].getName());
			}
		}
	}
	
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
/*	public Drive getDrive(){
		return drv;
	}*/
	public Content getSource(){
		return src;
	}
	public Devices getDevices(){
		return devs;
	}
	public long getBytesCopied(){
		return bytesCopied;
	}
	public String getProperty(String key){
		return config.getProperty(key);
	}
/*	public void setDrive(String s){
		drv = new Drive(s);
	}*/
	public void setSource(String s){
		src = new Content(s);
	}
	public String toString(){
		return src.toString() + "\n" + (devs.isMounted() ? 
				devs.getMountedDrive().toString() : "Drive Not Mounted.");
	}

}
