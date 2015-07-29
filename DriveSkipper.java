import java.io.*;
import java.util.ArrayList;


public class DriveSkipper{
  //process skippedDrives file per line excluding # comment lines and wrap grep around strings to remove call uuids
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
  
  private String formatList(String[] exList){
    //" | grep -v -e '/dev/sda' -e '/dev/sdb'")
    String hold = " | grep -v"; 
    for(int i=0; i<exList.length; i++){
      hold += " -e '" + exList[i] + "'";
    }
    return hold;
  }
  public String getExcludeString(){
    return excludeList;
  }
  public String[] getExcludeArray(){
    return excludeArray;
  }
  
  
}
