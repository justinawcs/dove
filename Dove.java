import java.io.*;
import java.nio.file.*;
import java.util.Properties;
//import java.util.ArrayList;

	//TODO consider which things to implement for initial launch, 
		//work on those items


public class Dove {
	private Content src;
	//private Drive  drv;
	private Devices devs;
	private Properties config = new Properties();
	private long bytesCopied = 0;
	private static int BUFFER_LENGTH = 1024;
	private static final String configLocation = System.getProperty("user.home")
			+ File.separator+".dove" + File.separator;

	public Dove(){
		/*
		 * Defaults are generated here:
		 * ex: mountLoc = config.getProperty("mountLocation", "/media/Dove");
		 *   pulls the data from the config file, but defaults to "/media/Dove"
		 *    if config file not found  
		 */
		loadConfigs(); //loads configs, if there its used, if not defaults
		String mountLoc, grepEx, contentLoc, folderName, allowNoThumb, 
			searchFileNames;
		mountLoc = config.getProperty("mountLocation", "/media/Dove");
		grepEx = config.getProperty("grepExcludes", "false");
		if(Boolean.parseBoolean(grepEx) == true){
			DriveSkipper ds = new DriveSkipper();
			grepEx = ds.getExcludeString();
		}else{
			grepEx = "";
		}
		contentLoc = config.getProperty("contentLocation","/home/");
		folderName = config.getProperty("folderName", "Dove");
		allowNoThumb = config.getProperty("allowNoThumbContent", "true");
		searchFileNames = config.getProperty("searchFileNames", "false");
		src = new Content(contentLoc, Boolean.parseBoolean(allowNoThumb));
		src.setSearchFileNames(Boolean.parseBoolean(searchFileNames));
		devs = new Devices(mountLoc, grepEx, folderName);
		
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
		//set bytesCopied to initial root before copying,
		//its already been been recreated/copied 
		bytesCopied = load.length(); 
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
			if(files[i].isDirectory()){
				Files.copy(files[i].toPath(), target.toPath() );
				bytesCopied += files[i].length();
				dive(files[i], bc + File.separator + files[i].getName());
			}else{
				chunkCopy(files[i], target);
			}
			System.out.print(files[i].toPath() + " : " );
			System.out.println(new File(bc + File.separator + 
					files[i].getName()).toPath() +" "+ files[i].length() );
			if(files[i].isDirectory()){
				
			}
		}
	}//TODO add code to kiosk to chunk update properly.
	//wont copy folders
	private void chunkCopy(File src, File tgt) throws IOException {
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try{
			in = new BufferedInputStream(new FileInputStream(src));
			out = new BufferedOutputStream(new FileOutputStream(tgt));
			int bit;
			byte[] buffer = new byte[BUFFER_LENGTH];
			while((bit = in.read(buffer)) != -1){
				out.write(buffer, 0, bit);
				bytesCopied += bit;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		finally{
			in.close();
			out.close();
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
