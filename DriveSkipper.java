import java.io.*;
import java.util.ArrayList;

/**
 * Process 'skippedDrives' to remove unneccessary drives from possible 
 * mountable drives. 
 * Reads per line, excluding #comments and wraps grep around strings to remove 
 * call uuids
 * -skipped File lists drive to be skipped by UUID
 * -excludeList holds result string
 * -excludeArray holds result array
 * -DEFAULTS defaults to skipping only the swap file
 * -DEFAULTS_ARRAY defaults to skipping only the swap file
 * @author Justin Williams
 * @version 0.0.8
 */
public class DriveSkipper{
  //TODO auto skip drive that contains root or similar
  /*
   * //blkid -c /dev/null | grep -v -e '/dev/sda' -e '/dev/sdb'
    //fdisk -l | grep 'Disk /' | grep -v -e '/dev/sda' -e '/dev/sdb'
    // consider sudo blkid -c /dev/null | grep -v -e "TYPE=\"swap\""
   * //String removeSwap = " | grep -v -e 'TYPE=\"swap\"'";
   */
  private File skipped = new File("skippedDrives");
  private String excludeList;
  private String[] excludeArray;
  private final String DEFAULTS = " | grep -v -e 'TYPE=\"swap\"'";
  private final String[] DEFAULTS_ARRAY = {"TYPE=\"swap\""};
  
  /**
   * Process 'skippedDrives' to remove unneccessary drives from possible 
   * mountable drives. 
   */
  public DriveSkipper(){
    try{//attempt load skippedfile
      FileReader reader = new FileReader(System.getProperty("user.home")
          +File.separator+".dove"+File.separator+ skipped);
      BufferedReader inputFile = new BufferedReader(reader);
      String line = inputFile.readLine(); //preloop prime
      ArrayList<String> data = new ArrayList<String>();
      while(line != null){//reads lines until End of file
        if(line.trim().startsWith("#") || line.length() == 0){
          //its a comment or blank line, it gets skipped
        }else{
          //Its real data
          data.add(line);
        }
        line = inputFile.readLine();
      }
      inputFile.close();
      data.trimToSize();
      String[] dataStr = new String[data.size()];
      for(int i=0; i<data.size(); i++){ //moves to array, prints
        dataStr[i] = data.get(i);
        System.out.println("[DriveSkipper]"+ dataStr[i]);
      }
      excludeList = formatList(dataStr);
      excludeArray = dataStr;
    }catch(IOException io){
      System.out.println("[DriveSkipper] 'skippedDrives' file not found. Using defaults.");
      excludeList = DEFAULTS;
      excludeArray = DEFAULTS_ARRAY;
      io.printStackTrace();
    }
  }
  
  /**
   * Format given list of uuids into grep-friendly string
   * @param exlist array of uuids
   * @returns string result
   */
  private String formatList(String[] exList){
    //" | grep -v -e '/dev/sda' -e '/dev/sdb'")
    String hold = " | grep -v"; 
    for(int i=0; i<exList.length; i++){
      hold += " -e '" + exList[i] + "'";
    }
    return hold;
  }
  
  /**
   * Returns grep-ready exlude list string.
   */
  public String getExcludeString(){
    return excludeList;
  }
  
  /**
   * Reurns grep-ready exlude string array.
   */
  public String[] getExcludeArray(){
    return excludeArray;
  }
}