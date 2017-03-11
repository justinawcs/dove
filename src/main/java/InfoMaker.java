import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
//import javax.imageio.ImageIO;

/**
 * Creates files that describe content to be read by Dove. Makes a human 
 * readable info.txt file, info.html and a binary data info.dat that will hold 
 * informational data about content. Name, origin, description, and boolean 
 * tags: video, audio, music, document, piture, other.
 * @author Justin Williams
 * @version 0.0.8
 */
public class InfoMaker {
  
  /**
   * Contstructor: takes content information as params and makes .txt and .dat 
   * files.
   * @param name Content name
   * @param org Content origin
   * @param desc Content description
   * @param vid boolean video
   * @param aud boolean audio
   * @param mus boolean music
   * @param doc boolean document
   * @param picboolean piture
   * @param other boolean other data
   * @param path absolute filepath 
   */
  public InfoMaker(String name, String org, String desc, boolean vid, 
        boolean aud, boolean mus, boolean doc, boolean pic, boolean other, 
        File path) throws IOException{
    Info result = new Info(name, org, desc, vid, aud, mus, doc, pic, other);
    writeOut(result, path);
  }
  
  /**
   * Overloaded Constructor:  takes content information as params and makes .txt
   * and .dat files.
   * @param result Info object
   * @param path absolute filepath
   */
  public InfoMaker(Info result, File path) throws IOException{
    writeOut(result, path);
  }
  
  /**
   * Overloaded Constructor:  takes content information and thumbnail image file
   * as params and makes .txt and .dat files. 
   * @param result Info object
   * @param path absolute filepath
   * @param img thumbnail file
   */
  public InfoMaker(Info result, File tgtPath, File img) throws IOException{
    writeOut(result, tgtPath, img);
    copyImg(tgtPath, img);
  }
  
  /**
   * Creates info.dat, .html, and .txt files with content information.
   * @param result Info object
   * @param path absolute filepath
   */
  public static void writeOut(Info data, File path) throws IOException{
    //info.dat
    System.out.println("[InfoMaker] "+ data.toString());
    System.out.print("Info:"+ path + File.separator + "info.dat, ");
    FileOutputStream out = new FileOutputStream(path.getAbsoluteFile() 
        + File.separator + "info.dat");
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
  
  /**
   * Creates info.dat, .html, and .txt files with content information includes 
   * thumbnail image.
   * @param result Info object
   * @param path absolute filepath
   * @param img thumbnail file
   */
  public static void writeOut(Info data, File path, File img) 
      throws IOException{
    //info.dat
    System.out.println("[InfoMaker] "+ data.toString());
    System.out.print("Info:"+ path + File.separator + "info.dat, ");
    FileOutputStream out = new FileOutputStream(path.getAbsoluteFile() 
      + File.separator + "info.dat");
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
  
  /**
   * Returns string of example filenames that will be created by this class.
   * @param path absolute filepath
   * @param img thumbnail file
   */
  public static String showExampleWriteOut(File path, File img){
    String str = path + File.separator + "info.dat\n";
      //str += path + File.separator + "info.txt\n";
      str += path + File.separator + "info.html\n";
      str += path + File.separator + "thumb"+(img.exists() ? 
          img.getName().substring(img.getName().lastIndexOf('.')) : ".jpg");
    return str;
  }
  
  /**
   * Copy image from given location to target location and renames to thumb.EXT,
   * where EXT is provided image extention, example: .jpg
   * @param path absolute filepath
   * @param img thumbnail file
   */
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
  
  /**
   * Converts/reduces image size to thumbnail appropiate dimensions.
   * @param path absolute filepath
   * @param img thumbnail file 
   */
  public static void convertImg(File path, File img){
    //TODO convert image using compression, move from InfoMakerGUI
  }
  
}
