import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.InputStream;

/**
 * Manages the finding and mounting filesystem to export Content into
 * @author Justin A. Williams
 * @version 0.0.8
 * 
 * - BLKID_CMD "blockid" linux command line program that prints drive info
 *     with a focus on:
 * - FDISK_CMD "fix Disk" linux command line program that print drive info
 *     with a focus on:
 * - mountLocation Disk Drive directory where the selected drives will be
 *     mounted
 * - grepExcludes string of drives that need to be removed from results of
 *     bklid command
 * - folderName given name of Content folder on device, on root of device
 *     example: MyDrive(E:)/Dove/ 
 * - mounted boolean, true if a device item has been mounted
 * - devs DeviceItem Array holds data of individual mountable devices
 * - drv holds inforamtion about currently mounted device
 */
public class Devices {
  private final static String BLKID_CMD = "blkid -c /dev/null";
  private final static String FDISK_CMD = "fdisk -l | grep 'Disk /'";
  //private final static String DMESG_CMD = "dmesg | grep 'usb-storage'";
  private String mountLocation;
  private String grepExcludes;
  private String folderName;
  private Integer mounted;
  private DeviceItem[] devs;
  private Drive drv;

  /**
   * Constructor: Creates and fills list of mountable devices.
   * @param mountLoc Disk Drive directory where the selected drives will be
   *     mounted 
   * @param grepEx string of drives that need to be removed from results of
   *     bklid command
   * @param name given name of Content folder on device, on root of device
   *     example: MyDrive(E:)/Dove/
   */
  public Devices(String mountLoc, String grepEx, String folder){
    mountLocation = mountLoc;
    grepExcludes = grepEx;
    folderName = folder;
    mounted = null;
    try{
      refresh();
    }catch(IOException io){
      System.out.println(io.getMessage() );
    }
  }
  
  /**
   * Executes bash commands and only returns outputs/results as String array. 
   * @param com command line as passed to shell
   * @return String array of command-line output/results
   */
  private static String[] execBash(String com) throws IOException{
    //create bash class to access p.stuff and prebuffer the data
    Process p = Runtime.getRuntime().exec(new String[] {
        //example: "bash", "-c", "/home/jaw/bin/java/Dove/DriveInfo.sh"
        "bash", "-c", com });
    try{
      p.waitFor();
      //p.exitValue()
      //p.getErrorStream()
      //p.getOutputStream()
    }catch(Exception e){
      System.out.println("Error Message:  " + e.getMessage() + "\n");
    }
    InputStream is = p.getInputStream();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(is));
    String s = null;
    ArrayList<String> data = new ArrayList<String>(); 
    while ((s = reader.readLine()) != null) {
      data.add(s);
      System.out.println("[Devices.execBash] "+s);
    }
    //System.out.println("[Devices.execBash] "+ data.size() + " items.");
    String[] out = new String[data.size()] ;
    for(int i=0; i<data.size(); i++ ){
      out[i] = data.get(i);
    }
    return out;
  }
  
  /**
   * Executes bash commands and returns exit value/code from given commmands, 
   * prints more information. 
   * @param com command line as passed to shell
   * @return exit code value of command
   */
  private static int execBashVerbose(String com) throws IOException{
    System.out.println(com);
    Process p = Runtime.getRuntime().exec(new String[] {
        //example: "bash", "-c", "/home/jaw/bin/java/Dove/DriveInfo.sh"
        "bash", "-c", com });
    try{
      p.waitFor();
      System.out.println("Exit Value: "+p.exitValue());
      System.out.println("Output:\n:"+ streamReader(p.getInputStream())); 
      System.out.println("Errors:\n"+ streamReader(p.getErrorStream()));
    }catch(Exception e){
      System.out.println("Error Message:  " + e.getMessage() + "\n");
    }
    InputStream is = p.getInputStream();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(is));
    String s = null;
    ArrayList<String> data = new ArrayList<String>(); 
    while ((s = reader.readLine()) != null) {
      data.add(s);
      System.out.println("[Devices.execBash] "+s);
    }
    //System.out.println("[Devices.execBash] "+ data.size() + " items.");
    String[] out = new String[data.size()] ;
    for(int i=0; i<data.size(); i++ ){
      out[i] = data.get(i);
    }
    return p.exitValue();
  }
  
  /**
   * Returns value of InputStream as a single string.
   * @param in InputStream
   * @returns String containing all data InputStream
   */
  private static String streamReader(InputStream in) {
    InputStreamReader inReader = new InputStreamReader(in);
    BufferedReader bufferedReader = new BufferedReader(inReader);
        String hold ="";
        try{
      String line = bufferedReader.readLine();
          while(line != null){
              hold = hold + line+ "\n";
              line = bufferedReader.readLine();
          }
        }catch (IOException io){
          io.printStackTrace();
        }
    return hold;
  }
  
  /**
   * Pulls infomation about available devices and generates list of valid ones.
   */
  public void refresh() throws IOException{
    //TODO Disable permanent devices or implement
    // run commands and pipe output to Devices(blkid,fdisk) 
    //blkid -c /dev/null | grep -v -e '/dev/sda' -e '/dev/sdb'
    //fdisk -l | grep 'Disk /' | grep -v -e '/dev/sda' -e '/dev/sdb'
    //consider sudo blkid -c /dev/null | grep -v -e "TYPE=\"swap\""
    //to remove swap mounts
    String[] location, label, size;
    String[] blkidData, fdiskData; 
    //String removeSwap = " | grep -v -e 'TYPE=\"swap\"'";
    //System.out.println("[Devices.refresh] "+ grepExcludes );
    blkidData = execBash(BLKID_CMD + grepExcludes);
    fdiskData = execBash(FDISK_CMD + grepExcludes );
    
    location = new String[blkidData.length];
    label = new String[blkidData.length];
    size = new String[blkidData.length];
    devs = new DeviceItem[blkidData.length];
    
    for(int i=0; i<blkidData.length; i++){
      StringTokenizer tok = new StringTokenizer(blkidData[i].trim(), 
          " :", false );
      // need /dev/BLAH, LABEL=BLAH
      while(tok.hasMoreTokens() ){
        String temp = tok.nextToken();
        while(temp.contains("\"") && temp.indexOf('"') == 
            temp.lastIndexOf('"')){
          temp = temp +" "+ tok.nextToken();
        }
        if(temp.startsWith("/dev/") ){
          location[i] = temp;
        }else if(temp.startsWith("LABEL=") ){
          label[i] = temp.substring(6);
        }else{
        }
      }
      if(null == label[i]){
        label[i] = "No Label";
      }
    }
    for(int i=0; i<fdiskData.length; i++){
      StringTokenizer tok = new StringTokenizer(fdiskData[i].substring(5),
          ":,", false );
      String dev = tok.nextToken().trim();
      String sz = tok.nextToken().trim();
      for(int j=0; j<location.length; j++){
        if(location[j].contains(dev) ){
          size[j] = sz;
        }
      }
    }
    for(int i=0; i<devs.length; i++){
      devs[i] = new DeviceItem(location[i], label[i], size[i]);
    }
    System.out.println("[Devices.refresh] "+ toString() );
  }
  
  /**
   * Mounts device by given integer.
   * @param array index of device
   * @returns boolean successful
   */
  public boolean mount(int index){
    //mount command
    if(isMounted()){
      unmount();
    }//TODO fix boolean succesful checks
    if(devs[index].isPermanent == false){
      try{
        execBash("mkdir " + mountLocation);
        String com = "mount " + devs[index].getLocation() +" "+ 
        mountLocation;
        execBashVerbose(com);
        /*
        Process p = Runtime.getRuntime().exec(new String[] {
          "bash", "-c",  com});
        try{
          p.waitFor();
        }catch(Exception e){
          System.out.println("Error Message:  " + e.getMessage() + 
              "\n");
        }
        //Check exit value of mount command 
        //and if mountlocation can be written to
        if(p.exitValue() != 0 || !new File(mountLocation).canWrite() ){
          System.out.println("Mount exit value: "+ p.exitValue());
          java.io.InputStream is = p.getErrorStream();
          java.io.BufferedReader reader = new java.io.BufferedReader(
              new InputStreamReader(is));
          String s = null;
          //ArrayList<String> data = new ArrayList<String>(); 
          while ((s = reader.readLine()) != null) {
            System.out.println("[Devices.mount.failure] "+s);
          }
          return false;
        }
        */
        
      }catch(IOException io){
        System.out.println(io.getMessage() );
      }
    drv = new Drive(mountLocation, folderName);
    System.out.println("[Devices.mount.success] "+drv.toString());
    }else {//devs[index].isPermanent == true
      //dont try to mount, its already there, just point
      drv = new Drive(devs[index].getLocation(), folderName );
    }
    mounted = index;
    return true;
  }
  
  //TODO decide unmount all on launch??
  /**
   * Unmounts currently mounted device.
   * @returns boolean successful
   *///TODO fix boolean successful
  public boolean unmount(){
    //unmount mounted drive
    if(getMounted().isPermanent() == false){
      //System.out.println("Permanent Mount");
      try{
        System.out.println("[Devices.unmount] "
          +getMounted().getLocation());
        execBashVerbose("umount "+ getMounted().getLocation() );
      }catch(IOException io){
        io.printStackTrace();
        System.out.println(io.getMessage() );
      }
    }else{ // getMounted.isPermanent == true
      //Dont try to unmount
    }
    drv = null;
    mounted = null;
    return true;
  }

  /**
   * Returns true if a device is mounted.
   * @returns boolean isMounted
   */
  public boolean isMounted(){
    return (mounted != null);
  }

  /**
   * Returns currently mounted DeviceItem.
   * @returns mounted DeviceItem
   */
  public DeviceItem getMounted(){
    if(mounted != null){
      return devs[mounted];
    }else{//mounted == null
      return null;
    }
  }
  
  /**
   * Returns the index that currently mounted device is in Device array.
   * @returns int array index
   */
  public int getMountedIndex(){
    if(mounted != null){
      return mounted;
    }else{//mounted == null
      return -1;
    }
  }
  
  /**
   * Returns currently mounted device's Drive object which gives information 
   * about the connected drive.
   * @returns Drive 
   */
  public Drive getMountedDrive(){
    return drv;
  }

  /**
   * Creates a Device and adds it to array. Very useful in testing.
   * @param loc absolute path to device
   * @param lbl label, name of device
   * @param sz size of device
   */
  public void addDevice(String loc, String lbl, String sz){
    //ex. DeviceItem more = new DeviceItem("/tmp/ramdisk/","Ramdisk","16M");
    deviceAdder(new DeviceItem(loc, lbl, sz) );
  }
  
  /**
   * Creates a Device and adds it to array. Very useful in testing.
   * @param loc absolute path to device
   * @param lbl label, name of device
   * @param sz size of device
   * @param perm boolean true for permanent Device that will not unmount
   */
   //Implement or delete
  public void addDevice(String loc, String lbl, String sz, boolean perm){
    deviceAdder(new DeviceItem(loc, lbl, sz, perm) );
  }
  
  /**
   * Creates a Device and adds it to array. Very useful in testing.
   * @param given DeviceItem to be added
   */
  private void deviceAdder(DeviceItem given){
    DeviceItem[] temp = new DeviceItem[devs.length+1];
    temp[0] = given;
    for(int i=0; i<devs.length; i++){
      temp[i+1] = devs[i];
    }
    devs = temp;
  }
  
  /**
   * Returns array containing strings that carry the name and size information
   * of available devices.
   * @returns String[] needed to choose drives.
   */
  public String[] getInfoArray(){
    String[] temp = new String[devs.length];
    for(int i=0; i<devs.length; i++){
      temp[i] = devs[i].getLabel() + " - " + devs[i].getSize();
    }
    return temp; 
  }
  
  /**
   * Returns a string that shows the status of Devices object/class
   * @returns String containing status info
   */
  public String toString(){
    String temp = "Is Mounted? " + isMounted() + " - " +
        (isMounted() ? getMounted().toString() : "N/A") ;
    for(int i=0; i<devs.length; i++){
      temp += "\n" + devs[i].toString() ;
    }
    return temp;
  }
  
  /**
   * Retruns string array of blkid output, 
   * @returns String array from command results
   */
  public static String[] getRawBlkidOutput(){
    String[] hold;
    try{
      hold = execBash(BLKID_CMD);
    }catch(IOException io){
      hold = null;
      io.printStackTrace();
    }
    return hold;
  }
  /**
   * Returns string array of fdisk output, 
   * @returns String array from command results
   */
  public static String[] getRawFdiskOutput(){
    String[] hold;
    try{
      hold = execBash(FDISK_CMD);
    }catch(IOException io){
      hold = null;
      io.printStackTrace();
    }
    return hold;
  }
  
  public static void main(String[] args){
    //unused
  }

  /**
   * INNER class to contain the pre-mount information from devices.
   * @author Justin A. Williams
   * @version 0.0.8
   * -location absolute path location
   * -label volume name, if available
   * -size estimated drive size
   * -isPermanent true if drive should not be unmounted
   */
  private class DeviceItem{
    private String location;
    private String label;
    private String size;
    private boolean isPermanent;
    //private File mountedLocation;
    //private boolean isMounted;
    
    /**
     * Standard Constructor.
     * @param loc absolute path location
     * @param lbl drive label
     * @param sz estimate total drive size
     */
    public DeviceItem(String loc, String lbl, String sz){
      location = loc;
      label = lbl;
      size = sz;
      isPermanent = false;
      //mountedLocation = new File(MOUNT_LOCATION); 
    }
    
    /**
     * Constructor with permanent mount option.
     * @param loc absolute path location
     * @param lbl drive label
     * @param sz estimate total drive size
     * @param perm true if drive should not be unmouted
     */
    public DeviceItem(String loc, String lbl, String sz, boolean perm){
      location = loc;
      label = lbl;
      size = sz;
      isPermanent = perm;
    }

    /**
     * Retruns full drive path.
     * @returns location string
     */
    public String getLocation() {
      return location;
    }
    
    /**
     * Returns label of drive.
     * @returns drive label string
     */
    public String getLabel() {
      return label;
    }
    
    /**
     * Returns estimate total drive size
     * @returns drive size string
     */
    public String getSize() {
      return size;
    }
    
    /**
     * Returns true if drive wont be unmounted
     */
    public boolean isPermanent() {
      return isPermanent;
    }

    /*
    public boolean isMounted() {
      return isMounted;
    }
    public void setMounted(boolean mtnd) {
      this.isMounted = mtnd;
    }*/
    
    /**
     * Returns current status 
     * @returns status string
     */
    public String toString(){
      return location +" : "+ label + " - " + size;
    }
  }
}
