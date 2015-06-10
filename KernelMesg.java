import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class KernelMesg {
	private boolean changed = true; //changeDetected
	private String lastTime = null; //time from last line of last update
	private ArrayList<String> list;
	public KernelMesg(){
		changed = update();
		System.out.println("Data changed: " + changed);
		
	}
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
	
	public boolean isChanged(){
		return changed;
	}
	
	public String getLastTime(){
		return lastTime;
	}
	
	public ArrayList<String> getList(){
		return list;
	}

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
