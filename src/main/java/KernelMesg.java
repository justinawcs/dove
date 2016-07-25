import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Detects if changes have been made to drives on system using the dmesg 
 * commmand in Linux. Drive changes could be insertion or removal of a device,
 * on detection on either boolean flag changed will turn true. The time string 
 * is logged to check for updates. An arraylist of of changes is kept.
 * @author Justin Williams
 * @version 0.0.8
 */
public class KernelMesg {
  private boolean changed = true; //changeDetected
  private String lastTime = null; //time from last line of last update
  private ArrayList<String> list;
  
  /**
   * Constructor: Starts log of system messages.
   */
  public KernelMesg(){
    changed = update();
    System.out.println("Data changed: " + changed);
  }
  
  /**
   * Updates by pulling message log and comparing it to last pull.
   * @returns true if changes to drive were made.
   */ 
  public boolean update(){
    ArrayList<String> temp = new ArrayList<String>() ;
    ArrayList<String> tempNewOnly = new ArrayList<String>();
    try {
      temp = execBash();
    } catch (IOException e) {
      // Catch file errors
      System.out.println("Command error:");
      e.printStackTrace();
    }
    temp.trimToSize();
    //set lastTime
    String endLine = temp.get(temp.size()-1); 
    boolean ok = endLine.contains("[") && 
            endLine.contains("]");
    if(lastTime == null && ok){
      String holder = endLine;
      //System.out.println(holder);
      holder = holder.substring( holder.indexOf("["),
          holder.indexOf("]") +1);
      System.out.println("first time: " + holder);
      lastTime = holder;
    }
    
    boolean afterLastTime = false;
    boolean changeDetected = false;
    String a = "";
    for(int i=0; i<temp.size(); i++){
      a = temp.get(i);
      if(a.startsWith(lastTime) && i < temp.size()-1 ){
        afterLastTime = true;
      }else if(afterLastTime){
        tempNewOnly.add(a);
        if(a.contains("USB disconnect") || a.contains("USB Mass Storage device detected" )){
          System.out.println(a);
          changeDetected = true;
        }
      }if(i == temp.size() -1){
        lastTime = a.substring( a.indexOf("["),
            a.indexOf("]") +1);
      }
    }
    list = tempNewOnly;
    return changeDetected;
  }
  
  /**Returns true if message log changed.*/
  public boolean isChanged(){
    return changed;
  }
  
  /**Returns string of last time reported by message log.*/
  public String getLastTime(){
    return lastTime;
  }
  
  /**Returns arraylist of changes to kernel logs.*/
  public ArrayList<String> getList(){
    return list;
  }
  
  /**
   * Returns arraylist of new lines from kernel logs.
   */
  private static ArrayList<String> execBash() throws IOException{
    final String com = "dmesg | grep -i 'usb' ";
    // file:  /var/log/dmesg
    Process p = Runtime.getRuntime().exec(new String[] {
        //example: "bash", "-c", "/home/jaw/bin/java/Dove/DriveInfo.sh"
        "bash", "-c", 
        com });
    try {
      //p.wait(); 
      //p.waitFor();
    }catch (Exception u){
      System.out.println("Error Message:  " + u.getMessage() + "\n");
    }
    java.io.InputStream is = p.getInputStream();
    java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
    java.io.InputStream es = p.getErrorStream();
    java.io.BufferedReader ereader = new java.io.BufferedReader(new InputStreamReader(es));
    
    String s = null, e = null;
    ArrayList<String> data = new ArrayList<String>();
    //s = reader.readLine();
    //while 
    while ((s = reader.readLine()) != null) {
      e = ereader.readLine();
      data.add(s);
      //System.out.println(s);
      //System.out.print((e != null)? "E:" + e+ "\n" :"" );
    }
    try{
      //System.out.println("Exit value: " +p.exitValue());
    }catch(IllegalThreadStateException a){
      a.printStackTrace();
    }
    return data;
  }
  
  /**
   * Main method: a built-in tester that will show if changes of drive insertion
   * and/or removal are properly reported, an interactive commandline. 
   */
  public static void main(String[] args){
    /*try {
      execBash();
    } catch (IOException e) {
      // Catch File errors
      e.printStackTrace();
    }*/
    Scanner key = new Scanner(System.in);
    KernelMesg km = new KernelMesg();
    boolean end = false;
    while(!end){
      System.out.print("Change drives and hit enter to update. Type q to quit. ");
      if(key.nextLine().equalsIgnoreCase("q")){
        end = true;
        System.out.println("Terminated.");
      }else{
        System.out.println("Data changed: " + km.update());
      }
      //km.update();
    }
    key.close();
  }
}
