import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;


public class Tester_New {
	public static void main(String[] args) throws IOException{
		
		//Dove one = new Dove();
		//one.setSource("/home/jaw/bin/Dove/content2/");
		//System.out.println(one.toString());
		//int count = one.getSource().getLength();
	//	for(int i=0;i<count;i++){
	//		System.out.println(one.getSource().getInfoAt(i).getName() +"\t"+
	//				one.getSource().getItemAt(i).hasImage() +"\t"
	//				/*one.getSource().getItemAt(i).getImage().toString() */ );
		//}
		//one.getSource().sortDate();
		//System.out.println(one.toString());
		//one.copy(2);
		/*
		one.getSource().sortAZ();
		System.out.println("Sorted Alphabetically:\n" + one.getSource().toString());
		one.getSource().sortSize();
		System.out.println("Sorted by Size:\n" + one.getSource().toString());
		one.getSource().sortDate();
		System.out.println("Sorted by Date:\n" + one.getSource().toString());
		one.getSource().setIgnoreNoInfo(true);
		one.getSource().refresh();
		System.out.println("Ingored No Content & Refreshed: \n" + 
							one.getSource().toString());
		*/
		/*//Search methods
		one.getSource().search("an");
		System.out.println("Searched for 'an' \n" + one.getSource().toString() );
		one.getSource().refresh();
		
		one.getSource().search("forever");
		System.out.println("Searched for 'forever' \n" + one.getSource().toString() );
		one.getSource().refresh();
		
		one.getSource().searchFilenames("Jumpy");
		System.out.println("Searched for 'Jumpy' \n" + one.getSource().toString() );
		one.getSource().refresh();
		
		one.getSource().searchFilenames("Tupac");
		System.out.println("Searched for 'Tupac' \n" + one.getSource().toString() );
		one.getSource().refresh();
		//*/
		
		//one.getSource().tagsAny(true, false, false, false, false, true);
		//System.out.println("Tags: Vid or Other \n" + one.getSource().toString() );
		//one.getSource().refresh();
		
		//one.getSource().tagsAll(false, false, true, true, false, false);
		//System.out.println("Tags: Vid and Other \n" + one.getSource().toString() );
		//one.getSource().refresh();
		
		//one.copy(4);
		/*try {
			one.copy(one.getSource().getFileAt(0));
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}*/
		//ContentItem first = new ContentItem(new File("/home/jaw/bin/content2/First"));
		//System.out.println(first.toString());
		
		//String t = String.valueOf(DoveGUI.percent(new Double(1.005d/100)) );
		//System.out.println(t);
		
		//Drive dr = new Drive("/tmp/ramdisk/");
		//dr.setupDrive("Pony");
		//File tempFile = File.createTempFile("Dove", ".txt");
		//System.out.println(tempFile.toString() );
		//tempFile.deleteOnExit();
		 
			// java.lang.Runtime rt = java.lang.Runtime.getRuntime();
	        // Start a new process: UNIX command ls
	        //java.lang.Process p = rt.exec("/home/jaw/bin/Golems.sh");
		
/*		Process p = Runtime.getRuntime().exec(new String[] {
			//"bash", "-c", "/home/jaw/bin/java/Dove/DriveInfo.sh"
			"bash", "-c", "ls -l"
		});
		try{
			p.waitFor();
		}catch(Exception e){
			System.out.println("Error Message:  " + e.getMessage() + "\n");
		}
		java.io.InputStream is = p.getInputStream();
        java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
        // And print each line
        String s = null;
        ArrayList<String> data = new ArrayList<String>(); 
        while ((s = reader.readLine()) != null) {
        	data.add(s);
            System.out.println(s);
        }
		System.out.println(data.size() + " items.");
		 //System.out.println("Working Directory = " +System.getProperty("user.dir"));
		 
		 // Show exit code of process
	        //System.out.println("Process exited with code = " + p.exitValue() );
*/	    
		
		//Devices d = new Devices();
		//System.out.println(d.toString() );
		//d.mount(0);
		//d.addDevice("/tmp/ramdisk/","Ramdisk","16MB");
		//System.out.println(d.toString() );
		//for(String a : d.getInfoArray() ){
		//	System.out.println(a);
		//}
		//d.unmount();
		//System.out.println("Unmounted..\n" + d.toString() );
		
		//Configs.getMountLoc() ;
		//Configs.getGrepExcludes() ;
		
		//string test
		String holder = "[10049.551966] scsi 10:0:0:0: Direct-Access              USB 2.0          1.0  PQ: 0 ANSI: 2";
		System.out.println(holder);
		int a = holder.indexOf("[");
		int b = holder.indexOf("]");
		System.out.println(a +" "+ b);
		holder = holder.substring(a ,b+1 );
		System.out.println(holder);
		//lastTime = holder;
	}
}
