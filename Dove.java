import java.awt.GraphicsEnvironment;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import java.util.Scanner;
//import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * Main class. Handles configuration, main copy function, and inner objects.
 * @author Justin A. Williams
 * @version 0.0.8
 */
public class Dove {
  private Content src;
  private Devices devs;
  private Properties config = new Properties();
  private long bytesCopied = 0;
  private static int BUFFER_LENGTH = 1024;
  private static final String CONFIG_LOCATION = System.getProperty("user.home")
      + File.separator + ".dove" + File.separator;
  //Default values
  private static final String DEF_MOUNT_LOCATION = "/media/Dove";
  private static final String DEF_GREP_EXCLUDES = "false";
  private static final String DEF_CONTENT_LOCATN = 
      System.getProperty("user.home") + File.separator + "Dove";
  private static final String DEF_FOLDER_NAME = "Dove";
  private static final String DEF_ALLOW_NO_THUMB_CONTENT = "true";
  private static final String DEF_SEARCH_FILE_NAMES = "false";
  
  /**
   * Starts configuration from either config file or defaults listed above.
   * example: mountLoc = config.getProperty("mountLocation", "/media/Dove");
   *   pulls the data from the config file, but defaults to "/media/Dove"
   *   if config file not found.
   */
  public Dove(){
    Boolean loaded = loadConfigs(); //loads configs, or defaults
    String mountLoc;
    String grepEx;
    String contentLoc;
    String folderName;
    String allowNoThumb;
    String searchFileNames;
    //Read from config files, or use final-defaults above 
    mountLoc = config.getProperty("mountLocation", DEF_MOUNT_LOCATION);
    grepEx = config.getProperty("grepExcludes", DEF_GREP_EXCLUDES);
    if(Boolean.parseBoolean(grepEx) == true){
      DriveSkipper ds = new DriveSkipper();
      grepEx = ds.getExcludeString();
    }else{
      grepEx = "";
    }
    contentLoc = config.getProperty("contentLocation", DEF_CONTENT_LOCATN);
    new File(contentLoc).mkdirs(); //Force creation of folder if not present
    folderName = config.getProperty("folderName", DEF_FOLDER_NAME);
    allowNoThumb = config.getProperty("allowNoThumbContent", 
        DEF_ALLOW_NO_THUMB_CONTENT);
    searchFileNames = config.getProperty("searchFileNames", 
        DEF_SEARCH_FILE_NAMES);
    // Call Source and Device class with configs
    src = new Content(contentLoc, Boolean.parseBoolean(allowNoThumb));
    src.setSearchFileNames(Boolean.parseBoolean(searchFileNames));
    devs = new Devices(mountLoc, grepEx, folderName);
    //System.out.println("[Dove] "+Configs.toString());
  }
  
  /**
   * Calls config into Properties object, returns boolean of successful load 
   *  from config file
   * @return true if configs file is loaded, false if not.
   */
  private boolean loadConfigs(){
    try{
      config.load(new FileInputStream(CONFIG_LOCATION + File.separator +
          "config.cfg"));
      System.out.println("[Dove] Config file successfully loaded: " +
          config.toString() + 
          "\n" + CONFIG_LOCATION);
      return true;
    }catch(IOException io){
      System.out.println("[Dove] config.cfg - Not Found! " + CONFIG_LOCATION);
      //io.printStackTrace();
      // add throw user notification
      noConfigNotify();
      
      return false;
    }
  }
  
  /**
   * Explains to user that Config file was not loaded. Prints default config.
   * Suggests the termination of this program and running of ConfigWizard. 
   */
  private void noConfigNotify(){
    //TODO move GUI section to DoveDUI after DoveGUI extends Dove
    final String WARN = "WARNING:  No configuration file found for Dove. "
      + "\nThis program will likely not work as intended without " 
      + "proper configuration. Please run ConfigWizard to setup Dove.";
    if(GraphicsEnvironment.isHeadless()){
      //there is no GUI, text based warning and option.
      Scanner key = new Scanner(System.in);
      String ans;
      System.out.println(WARN + "\nPress q to quit, or press enter to " +
          "acknowledge and continue.");
      ans = key.nextLine();
      if(ans.startsWith("q")){
        System.exit(0);
      }
    }else{
      //GUI present use JOptionPane to warn user, then continue
      JOptionPane.showMessageDialog(null, WARN, "Configuration", 
      JOptionPane.WARNING_MESSAGE, null);
      //hit ok continues, exit closes program
    }
  }
  
  /**
   * Checks everything to make sure that copy will go smoothly,
   * before copying, returns boolean "all clear"
   * Conditions: list occupied, list size okay, drive mounted
   * @param index    given ContentItem index item to check
   * @return         true if index passes checks, else false
   */
  public boolean preCopy(int index){
    // TODO implement or delete, Move code from Copy page
    
    return true;
  }
  
  /**
   * Copies content at given array index, updates bytesCopied, returns 
   * true if sucessful.
   * @param index    index location of Content array
   * @return         true if copy succesfully completes, false on fail
   * @throws IOException if file not found/ copy problmes
   */
  public boolean copy(int index)throws IOException{ 
    //make new Folder on drive
    //loop that copies all entries in folder
    File load = src.getFileAt(index);
    return copyBase(load);
  }
  
  /**
   * Copies given content item, upadtes bytesCopied, returns 
   * true if sucessful.
   * @param item     ContentItem
   * @return         true if copy succesfully completes, false on fail
   * @throws IOException if file not found/ copy problmes
   */
  public boolean copy(ContentItem item)throws IOException{ 
    //make new Folder on drive
    //loop that copies all entries in folder
    File load = item.getFile();
    return copyBase(load);
  }
  
  /**
   * Handles work of Copy functions. 
   * @param load      ContentItem folder location for copying
   * @return          true if copy succesfully completes, false on fail
   * @throws IOException if file not found/ copy problmes
   */
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
  //TODO Create new copy base that counts bytes, use code from Tester_Copy
  
  /**
   * Recurisve copy engine; Traverses directory to copy every file/folder 
   * @param f      Initial folder whose content will be copied entirely
   * @param bc     breadcrumbs, keeps track of depth of folders 
   * @throws IOException if file not found/ copy problmes
   */
  private void dive(File f, String bc) throws IOException{
    File files[] = f.listFiles();
    for(int i=0; i<files.length; i++){
      File target = new File(bc + File.separator + files[i].getName() );
      target.setWritable(true);
      if(files[i].isDirectory() ){ // Folder: Chunkcopy cannot copy folders
        Files.copy(files[i].toPath(), target.toPath() );
        bytesCopied += files[i].length();
        dive(files[i], bc + File.separator + files[i].getName());
      }else{ // File: Chunck them files
        chunkCopy(files[i], target);
      }
      System.out.print(files[i].toPath() + " : " );
      System.out.println(new File(bc + File.separator + 
          files[i].getName()).toPath() +" "+ files[i].length() );
      if(files[i].isDirectory()){
        
      }
    }
  }//TODO add code to kiosk to chunk update properly.
  /**
   * Splits files into small chunks of given length and copies them to allow
   *     time tracking of process. Cannot copy folders.
   * @param src Source File being copied
   * @param tgt Destination file location
   * @throws IOException 
   * @see BUFFER_LENGTH
   */
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
  
  /**
   * Converts long byte length to readable string 
   * @param bytes     raw number of bytes
   * @param si        true-1000 SI based; false-1024 binary based
   * @return          bytes count to one decimal place
   */
  public static String humanReadableByteCount(long bytes, boolean si) {
      int unit = si ? 1000 : 1024;
      if (bytes < unit) return bytes + " B";
      int exp = (int) (Math.log(bytes) / Math.log(unit));
      String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
      return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }
  
  /**
   * Returns content source object.
   * @return     Content class object
   */
  public Content getSource(){
    return src;
  }
  
  /**
   * Returns devices object. 
   * @return    Devices class object
   */
  public Devices getDevices(){
    return devs;
  }
  
  /**
   * Relays the amount of bytes that have been copied to track progress
   * @return     number of bytes that have been copied already
   */
  public long getBytesCopied(){
    return bytesCopied;
  }
  
  /**
   * Returns data from the config file
   * @param key    text key to retrieve corresponding data
   * @return       corresponding data
   */
  public String getProperty(String key){
    return config.getProperty(key);
  }

  /**
   * Sets the Source of Content
   * @param s    File path sting to new Content location
   */
  public void setSource(String s){
    src = new Content(s);
  }
  
  /**
   * Gives current data of class
   * @reutrn     relevant data
   */
  public String toString(){
    return src.toString() + "\n" + (devs.isMounted() ? 
        devs.getMountedDrive().toString() : "Drive Not Mounted.");
  }
}
