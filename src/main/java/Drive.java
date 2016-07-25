import java.io.*;

/**
 * Maintains drive after it has been mounted.
 * @author Justin A. Williams
 * @version 0.0.8
 * -drv base folder location of drive
 * -foldername Folder name that will store content on drive, example: Dove
 * -totalSpace accurate byte count of all space on drive
 * -freeSpace accurate byte count of used space on drive
 * -isSetup true if drive has been used before at Dove terminal, 
 *    has a folder named by folderName
 */
public class Drive {
  private File drv;
  private String folderName;
  private long totalSpace;
  private long freeSpace; // Usable space
  private boolean isSetup;
  
  /**
   * Constructor. Holds info and checks setup.
   * @param path absolute path of drive
   * @param fName Folder name that will store content on drive, example: Dove
   */
  public Drive(String path, String fName){
    drv = new File(path);
    folderName =  fName;
    totalSpace = drv.getTotalSpace();
    freeSpace = drv.getUsableSpace();
    checkSetup();
  }
  
  /**
   * Refreshes drive space info and checks if drive has been setup.
   */
  public void refresh(){
    totalSpace = drv.getTotalSpace();
    freeSpace = drv.getUsableSpace();
    checkSetup();
  }
  
  /**
   * Checks if drive is setup and ready to recieve files.
   * General overloaded method for checkSetup("FolderName")
   * @param name folderName string
   * @returns true if check successful
   */
  public boolean checkSetup(){
    return checkSetup(folderName);
  }
  
  /**
   * Checks if given drive is setup and ready to recieve files.
   * Conditions: Folder exists
   * //TODO add reciept file found
   * @param name folderName string
   * @returns true if check successful
   */
  public boolean checkSetup(String name){
    boolean check = false;
    boolean exist = false;
    //if [drive]/Dove/ exists then isSetup = true
    //final String dove = "Dove"; // added to 
    //final String doveDat = "Dove.dat"; //no longer check for Dove.dat
    //try{
      exist = drv.exists();
      if(!exist){
        System.out.println("Drive does not exist.");
      }
    /*}*/
    if(exist){
      for(int i=0; i<drv.list().length; i++){
      if(drv.list()[i].equals(name) && drv.listFiles()[i].isDirectory()){
        check = true;	
      }else{// Just looping through.
      }
      /*else if(drv.listFiles()[i].isDirectory()){
      //Folder name is Not "DOVE" or given then look for renamed 
       * folder in device root
        File[] folder = drv.listFiles()[i].listFiles();
        for(File f: folder){
          if(f.isFile() && f.getName().equals(doveDat) ){
            check = true;
          }
        }
      }*/
      }
    }
    isSetup = check;
    if(!isSetup){
      System.out.println("Drive not properly setup.");
      // In GUI ask for name of Dove Folder
      //use setupDrive(string)
    }
    return isSetup;
  }
  
  /**
   * Places proper folder on drive and sets flag as setup.
   * General overloaded method for setupDrive("FolderName")
   * @param s folderName string
   * @returns true if setup successful
   */
  public void setupDrive(){
    // Place Dove/ into base of drive
    new File(drv.toString() + File.separator + folderName).mkdir();
    //upload preset content if required
    isSetup = true;
  }
  
  /**
   * Places given folder on drive and sets flag as setup.
   * @param s folderName string
   * @returns true if setup successful
   */
  public boolean setupDrive(String s){
    boolean flag = false;
    for(String i: drv.list()){
      if(s.equals(i)){
        flag = true;
      }
    }
    if(flag == true){
      System.out.println(s + " is already taken as a folder name!");
      return false;//false for operation could not complete!
    }else{
      drv = new File(drv.toString() + File.separator + s);
      drv.mkdir();
      folderName = s;
      isSetup = true;
      return true;
    }
  }
  
  /**
   * Returns true if drive is setup and ready to recieve files.
   */
  public boolean isSetup(){
    return isSetup;
  }
  
  /**
   * Returns total space in bytes on drive 
   */
  public long getTotalSpace(){
    return totalSpace;
  }
  
  /**
   * Returns free space in bytest on drive.
   */
  public long getFreeSpace(){
    return freeSpace;
  }
  
  /**
   * Returns file object for drive folder.
   */
  public File getDrive(){
    return drv;
  }
  
  /**
   * Returns target folder on drive; Dove folder. 
   */
  public File getDoveFile(){
    File temp = new File(drv.getAbsolutePath() + File.separator 
        + folderName);
    return temp;
  }
  
  /**
   * Returns percent of free space remaining on drive.
   */
  public double getPercentRem(){
    return ((double)freeSpace / (double)totalSpace) * 100d;
  }
  
  /**
   * Returns percent of free space remaining onn drive after adding in given 
   * amount of data, given in bytes.
   * @param used amount of data that will be copied onto drive
   */
  public double getPercentRem(long used){
    long left = freeSpace - used;
    return ((double)left / (double)totalSpace) * 100d;
  }
  
  /**
   * Returns a string of current status. 
   * @returns status string
   */
  public String toString(){
    String temp = drv.getAbsolutePath() + "\n";
    temp += freeSpace + " / " + totalSpace + "\n";
    temp += getPercentRem() +"%\n";
    temp += "Drive Setup: " + isSetup + "\n";
    for(String i: drv.list()){
      temp += i + "\n";
    }
    return temp;
  }
}
