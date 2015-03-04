package Legacy;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class Base {
	private static File content = new File("/home/jaw/bin/content","/");

	public static void main(String[] args){
		System.out.println(content);
		System.out.println(content.isDirectory());
		System.out.println(content.getUsableSpace());
		String[] list = content.list();
		for(int i=0;i<list.length;i++){
			System.out.println(i + ": " + list[i]);
		}
		//Path drive = Paths.get("/home/jaw/bin/drive/trgt.txt");
		File drive = new File("/home/jaw/bin/drive/trgt.txt");
		//Path src = Paths.get(content.getAbsolutePath());
		//Path src = Paths.get("/home/jaw/bin/content/jaw.txt");
		File src = new File("/home/jaw/bin/content/jaw.txt");
		try {
			Files.copy(src.toPath(), drive.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Make and index selection.");
		int cho = keyboard.nextInt();
		keyboard.close();
		String srcPath = "/home/jaw/bin/content/";
		Path choice = Paths.get(srcPath+list[cho]);
		Path drv2 = Paths.get("/home/jaw/bin/drive/choice.txt");
		try{
			Files.copy(choice, drv2, StandardCopyOption.REPLACE_EXISTING);
			System.out.println(list[cho] + " Has been moved.");
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("Printing folder contents:");
		for(String j : list){
			File temp = new File(content.toString() + j);
			System.out.println(temp.toString());
		}
		
	}
}
