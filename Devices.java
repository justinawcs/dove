import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class Devices {
	private final static String BLKID_CMD = "blkid -c /dev/null";
	private final static String FDISK_CMD = "fdisk -l | grep 'Disk /'";
	//private final static String DMESG_CMD = "dmesg | grep 'usb-storage'";
	private String mountLocation;
	private String grepExcludes;
	private Integer mounted;
	private DeviceItem[] devs;
	private Drive drv;

	
	public Devices(String mountLoc, String grepEx){
		mountLocation = mountLoc;
		grepExcludes = grepEx;
		mounted = null;
		try{
			refresh();
		}catch(IOException io){
			System.out.println(io.getMessage() );
		}
	}
	
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
		java.io.InputStream is = p.getInputStream();
		java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
		String s = null;
		ArrayList<String> data = new ArrayList<String>(); 
		while ((s = reader.readLine()) != null) {
			data.add(s);
			//System.out.println("[Devices.execBash] "+s);
		}
		//System.out.println("[Devices.execBash] "+ data.size() + " items.");
		String[] out = new String[data.size()] ;
		for(int i=0; i<data.size(); i++ ){
			out[i] = data.get(i);
		}
		return out;
	}
	
	public void refresh() throws IOException{
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
			StringTokenizer tok = new StringTokenizer(blkidData[i].trim(), " :", false );
			// need /dev/BLAH, LABEL=BLAH
			while(tok.hasMoreTokens() ){
				String temp = tok.nextToken();
				while(temp.contains("\"") && temp.indexOf('"') == temp.lastIndexOf('"')){
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
			StringTokenizer tok = new StringTokenizer(fdiskData[i].substring(5), ":,", false );
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
	public boolean mount(int index){
		//mount command
		if(devs[index].isPermanent == false){
			try{
				execBash("mkdir " + mountLocation);
				String com = "mount " + devs[index].getLocation() +" "+ mountLocation;
				Process p = Runtime.getRuntime().exec(new String[] {
					"bash", "-c",  com});
				try{
					p.waitFor();
				}catch(Exception e){
					System.out.println("Error Message:  " + e.getMessage() + "\n");
				}
				//Check exit value of mount command and check if mountlocation can be written to
				if(p.exitValue() != 0 || !new File(mountLocation).canWrite() ){
					System.out.println("Mount exit value: "+ p.exitValue());
					java.io.InputStream is = p.getErrorStream();
					java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
					String s = null;
					//ArrayList<String> data = new ArrayList<String>(); 
					while ((s = reader.readLine()) != null) {
						System.out.println("[Devices.mount.failure] "+s);
					}
					return false;
				}
				
			}catch(IOException io){
				System.out.println(io.getMessage() );
			}
			drv = new Drive(mountLocation);
		}else {//devs[index].isPermanent == true
			//dont try to mount, its already there, just point
			drv = new Drive(devs[index].getLocation() );
		}
		mounted = index;
		return true;
	}
	public boolean unmount(){
		//unmount mounted drive
		if(getMounted().isPermanent() == false){
			//System.out.println("Permanent Mount");
			try{
				execBash("umount "+ getMounted().getLocation() );
			}catch(IOException io){
				System.out.println(io.getMessage() );
			}
		}else{ // getMounted.isPermanent == true
			//Dont try to unmount
		}
		drv = null;
		mounted = null;
		return true;
	}

	public boolean isMounted(){
		return (mounted != null);
	}

	public DeviceItem getMounted(){
		if(mounted != null){
			return devs[mounted];
		}else{//mounted == null
			return null;
		}
	}
	public int getMountedIndex(){
		if(mounted != null){
			return mounted;
		}else{//mounted == null
			return -1;
		}
	}
	public Drive getMountedDrive(){
		return drv;
	}

	public void addDevice(String loc, String lbl, String sz){
		//ex. DeviceItem more = new DeviceItem("/tmp/ramdisk/","Ramdisk","16M");
		deviceAdder(new DeviceItem(loc, lbl, sz) );
	}
	public void addDevice(String loc, String lbl, String sz, boolean perm){
		deviceAdder(new DeviceItem(loc, lbl, sz, perm) );
	}
	private void deviceAdder(DeviceItem given){
		DeviceItem[] temp = new DeviceItem[devs.length+1];
		temp[0] = given;
		for(int i=0; i<devs.length; i++){
			temp[i+1] = devs[i];
		}
		devs = temp;
	}
	
	public String[] getInfoArray(){
		String[] temp = new String[devs.length];
		for(int i=0; i<devs.length; i++){
			temp[i] = devs[i].getLabel() + " - " + devs[i].getSize();
		}
		return temp; 
	}
	public String toString(){
		String temp = "Is Mounted? " + isMounted() + " - " +
				(isMounted() ? getMounted().toString() : "N/A") ;
		for(int i=0; i<devs.length; i++){
			temp += "\n" + devs[i].toString() ;
		}
		return temp;
	}
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
		
	}
	/**
	 * @author jaw
	 *
	 */
	private class DeviceItem{
		private String location;
		private String label;
		private String size;
		private boolean isPermanent;
		//private File mountedLocation;
		//private boolean isMounted;
		
		public DeviceItem(String loc, String lbl, String sz){
			location = loc;
			label = lbl;
			size = sz;
			isPermanent = false;
			//mountedLocation = new File(MOUNT_LOCATION); 
		}
		
		public DeviceItem(String loc, String lbl, String sz, boolean perm){
			location = loc;
			label = lbl;
			size = sz;
			isPermanent = perm;
		}

		public String getLocation() {
			return location;
		}
		public String getLabel() {
			return label;
		}
		public String getSize() {
			return size;
		}
		public boolean isPermanent() {
			return isPermanent;
		}

/*		public boolean isMounted() {
			return isMounted;
		}
		public void setMounted(boolean mtnd) {
			this.isMounted = mtnd;
		}
*/
		public String toString(){
			return location +" : "+ label + " - " + size;
		}
	}
}
