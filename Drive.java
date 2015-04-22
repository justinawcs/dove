import java.io.*;

public class Drive {
	private File drv;
	private long totalSpace;
	private long freeSpace; // Usable space
	private boolean isSetup;
	
	public Drive(String s){
		drv = new File(s);
		totalSpace = drv.getTotalSpace();
		freeSpace = drv.getUsableSpace();
		checkSetup();
	}
	
	public void refresh(){
		totalSpace = drv.getTotalSpace();
		freeSpace = drv.getUsableSpace();
		checkSetup();
	}
	
	public void checkSetup(){
		checkSetup("Dove");
	}
	public void checkSetup(String name){
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
		//}
		if(exist){
			for(int i=0; i<drv.list().length; i++){
			if(drv.list()[i].equals(name) && drv.listFiles()[i].isDirectory()){
				check = true;	
			}else{// Just looping through.
			}
			/*else if(drv.listFiles()[i].isDirectory()){
			//Folder name is Not "DOVE" or given then look for renamed folder in device root
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
	}
	
	public void setupDrive(){
		// Place Dove/ into base of drive
		new File(drv.toString() + File.separator + "Dove").mkdir();
		//upload preset content if required
		isSetup = true;
	}
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
			isSetup = true;
			return true;
		}
	}
	
	public boolean isSetup(){
		return isSetup;
	}
	
	public long getTotalSpace(){
		return totalSpace;
	}
	public long getFreeSpace(){
		return freeSpace;
	}
	
	public File getDrive(){
		return drv;
	}
	public File getDoveFile(){
		File temp = new File(drv.getAbsolutePath() + File.separator + "Dove");
		return temp;
	}
	
	public double getPercentRem(){
		return ((double)freeSpace / (double)totalSpace) * 100d;
	}
	
	public double getPercentRem(long used){
		long left = freeSpace - used;
		return ((double)left / (double)totalSpace) * 100d;
	}
	
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
