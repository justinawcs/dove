import java.io.*;
import java.nio.file.*;
import java.util.Properties;
//import java.util.ArrayList;

	//TODO consider which things to implement for initial launch, 
		//work on those items
/*  Relief Kiosk
 *  --DoveCLI
 * 	--InfoMakerCLI
 	--drive Skipper class
 	--thumbnail only button
 	--file list on GUI
 	--'df -h' to get partition size estimates? --> df only reads mounted fs, 
 		wont work.
 	--config and info config setters -> Wizards
 	--tui and gui for config.cfg file
 	--look for config files in home directory, System.getProperty("user.home");
 	--move dove.txt info into header of html file for fewer files.
 	--how can program ask for root/admin->> 
 	wrap loaders in shell script that calls gksudo
 	--search-no popup, takeover center stage
 	--ContentItem arraylist of filenames
 	in full kiosk preload thumbs for speed over memory, eventually
 	--splash page on DoveGUI with loading bar, need RS
 	>>code to listen for storage devices, esp. removed before unmount
 	--list page needs undo button
 	--thumb compression-better
 	--infomaker editing, better/smoother Change Listener- called on everychagne
 	 	to keep everything updated?
 	wizard need explainations in main window
 	--fix tags search
 	--thumbsnails only button
 	--XXrestrict sort type to after type is chosen only, when clear go default
 	--infomakergui overwirtes?
 	--XXadd sort best type
 	--align thumbnails plz
 	Copy Progress Bar
 	help/about panels
 	DoveKiosk breadcrumbs at top
 	-- bash code-better make class->> nope just use standard better
 	Documentation and information
 	prelim website
 	ask reddit for project name
 	check to see if filesizes can fit on filesystem and disable files that 
 		cannot
 	--add config to set name of Dove folder on dest. drive
 	ability to change settings from inside program and restart
 */

public class Dove {
	private Content src;
	//private Drive  drv;
	private Devices devs;
	private Properties config = new Properties();
	private long bytesCopied = 0;
	private final String configLocation = System.getProperty("user.home")
			+File.separator+".dove"+File.separator;

	public Dove(){
		/*
		 * Defaults are generated here:
		 * ex: mountLoc = config.getProperty("mountLocation", "/media/Dove");
		 *   pulls the data from the config file, but defaults to "/media/Dove"
		 *    if config file not found  
		 */
		loadConfigs(); //loads configs, if there its used, if not defaults
		String mountLoc, grepEx, contentLoc, allowNoThumb, searchFileNames;
		mountLoc = config.getProperty("mountLocation", "/media/Dove");
		grepEx = config.getProperty("grepExcludes", "false");
		if(Boolean.parseBoolean(grepEx) == true){
			DriveSkipper ds = new DriveSkipper();
			grepEx = ds.getExcludeString();
		}else{
			grepEx = "";
		}
		contentLoc = config.getProperty("contentLocation","/home/");
		allowNoThumb = config.getProperty("allowNoThumbContent", "true");
		searchFileNames = config.getProperty("searchFileNames", "false");
		src = new Content(contentLoc, Boolean.parseBoolean(allowNoThumb));
		src.setSearchFileNames(Boolean.parseBoolean(searchFileNames));
		devs = new Devices(mountLoc, grepEx);
		//System.out.println("[Dove] "+Configs.toString());
	}

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
