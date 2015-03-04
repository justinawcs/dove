import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
//import javax.imageio.ImageIO;
public class InfoMaker {

	public InfoMaker(String name, String org, String desc, boolean vid, boolean aud,
					boolean mus, boolean doc, boolean pic, boolean other, File path) throws IOException{
		Info result = new Info(name, org, desc, vid, aud, mus, doc, pic, other);
		writeOut(result, path);
	}
	public InfoMaker(Info result, File path) throws IOException{
		writeOut(result, path);
	}
	
	public InfoMaker(Info result, File tgtPath, File img) throws IOException{
		writeOut(result, tgtPath, img);
		copyImg(tgtPath, img);
	}
	
	public static void writeOut(Info data, File path) throws IOException{
		//info.dat
		System.out.println("[InfoMaker] "+ data.toString());
		System.out.print("Info:"+ path + File.separator + "info.dat, ");
		FileOutputStream out = new FileOutputStream(path.getAbsoluteFile() + File.separator + "info.dat");
		ObjectOutputStream outputFile = new ObjectOutputStream(out);
		outputFile.writeObject(data);
		outputFile.close();
		//info.html, w/ no thumb image
		System.out.print(path + File.separator + "info.html\n");
		FileWriter fw = new FileWriter(path + File.separator + "info.html");
		PrintWriter print = new PrintWriter(fw);
		print.println(data.toHtml());
		print.close();
		
		
	}
	public static void writeOut(Info data, File path, File img) throws IOException{
		//info.dat
		System.out.println("[InfoMaker] "+ data.toString());
		System.out.print("Info:"+ path + File.separator + "info.dat, ");
		FileOutputStream out = new FileOutputStream(path.getAbsoluteFile() + File.separator + "info.dat");
		ObjectOutputStream outputFile = new ObjectOutputStream(out);
		outputFile.writeObject(data);
		outputFile.close();
		//info.dat w/ thumb image
		String ext = img.getName();
		ext = ext.substring(ext.lastIndexOf(".") +1 );//after the period
		System.out.println("thumb." + ext);
		System.out.println(path + File.separator + "info.html");
		FileWriter fw = new FileWriter(path + File.separator + "info.html");
		PrintWriter print = new PrintWriter(fw);
		print.println(data.toHtml(ext) );
		print.close();
	}
	
	public static String showExampleWriteOut(File path, File img){
		String str = path + File.separator + "info.dat\n";
			//str += path + File.separator + "info.txt\n";
			str += path + File.separator + "info.html\n";
			str += path + File.separator + "thumb"+(img.exists() ? 
					img.getName().substring(img.getName().lastIndexOf('.')) : ".jpg");
		return str;
	}
	
	public static void copyImg(File path, File img) throws IOException{
		// just copy image 
		String type = img.getName();
		//String type;
		int split = type.lastIndexOf(".");
		type = type.substring(split);
		
		
		
		
		File output = new File(path.toString() + File.separator + "thumb" + type);
		System.out.println(output.toString());
		//ImageIO.write(ImageIO.read(img), "png", output);
		
		if(output.exists()){
			File temp = new File(path.toString() + File.separator 
					+ "OLD_" + output.getName() );
			output.renameTo(temp);
			System.out.println("Renamed to: " + temp.toString());
		}else{
			File[] list = path.listFiles();
			for(int i=0; i<list.length; i++){
				if(list[i].getName().startsWith("thumb.")){
					list[i].renameTo(new File(path.toString() + File.separator 
					+ "OLD_" + list[i].getName()));
				}
			}
			
		}
		Files.copy(img.toPath(), output.toPath() );
	}
	
	public static void convertImg(File path, File img){
		//TODO convert image using compression
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		// Creates a java readable file: info.dat
		// and a human readable info.txt
		String name, org, desc;
		boolean vid, aud, mus, doc, pic, other;
		Scanner key = new Scanner(System.in);
		
		System.out.println("Provide the following information about the content.");
		System.out.println("Name? ");
		name = key.nextLine();
		//key.nextLine();
		
		System.out.println("Origin? ");
		org = key.nextLine();
		//key.nextLine();
		
		System.out.println("Description? ");
		desc = key.nextLine();
		//key.nextLine();
		
		System.out.println("Video? ");
		vid = key.nextBoolean();
		
		System.out.println("Audio? ");
		aud = key.nextBoolean();
		
		System.out.println("Music? ");
		mus = key.nextBoolean();
		
		System.out.println("Document? ");
		doc = key.nextBoolean();
		
		System.out.println("Pictures? ");
		pic = key.nextBoolean();
		
		System.out.println("Other? ");
		other = key.nextBoolean();
		key.close();
		System.out.println();
		Info result = new Info(name, org, desc, vid, aud, mus, doc, pic, other);
		File currentDir = new File(System.getProperty("user.dir"));
		
		if(args.length > 0 ){
			File thumb = new File(args[0]);
			if(thumb.isFile()){
				System.out.println("Thumbnail Image Filename: "+args[0]);
				writeOut(result, currentDir,new File(args[0]));
				copyImg(currentDir, new File(args[0]));
			}
		}else{
			writeOut(result, currentDir);
		}
		System.out.println("Data created successfully.");
		/* Block moved to writeOut()
		System.out.println(result.toString());
		FileOutputStream out = new FileOutputStream("info.dat");
		ObjectOutputStream outputFile = new ObjectOutputStream(out);
		outputFile.writeObject(result);
		outputFile.close();
		
		FileWriter fw = new FileWriter("info.txt");
		PrintWriter print = new PrintWriter(fw);
		print.println(result.toString());
		print.close();
		*/
		
	}

}
