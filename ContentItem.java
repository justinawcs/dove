import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * ContentItem class object maintains information from the Dove file and 
 * included files.
 * @author Justin A. Williams
 * @version 0.0.8
 */
public class ContentItem{
  private File item; //Content Folder 
  private long size; //Total file size
  private Date date; //Creation Date
  private Info info; //Given info from data file
  private boolean hasData;
  private boolean hasImage; 
  private Image img; //thumbnail image
  private String imgType = ""; //File extension of thumbnail
  private long imgSize;
  private ArrayList<String> names = new ArrayList<String>(); //Filelist names
  
  /**
   * Constructor: takes File object given and tests it to see if it is valid 
   * Dove content and/or has a thumbnail image.
   * @param it ContentItem file(folder) location
   */
  public ContentItem(File it){
    item = it;
    hasData = testData(); //sets hasData, hasImage, imgType 
    size = dive(item);
    if(it.isDirectory()){
      makeNames(it, "");
    }
    if(hasData){
      makeInfo(); // date and info
    }else{//There is no info and dates to get so they remain empty
      info = null;
      date = null;
    }
    if(hasImage){
      try{
        System.out.print("[ContentItem] Fetching img: " 
            + item.getAbsolutePath() );
        img = ImageIO.read(new File(item.getAbsolutePath() + File.separator + 
        "thumb." + imgType ));
        System.out.println(" - Done.");
      }catch(IOException e){
      System.out.println("Invalid thumb"); //should never get thrown
      }
    }else{//There is no image so null pointer
      img = null;
    }
    System.out.println("[ContentItem] "+ toString());
  }
  
  /**
   * Recursive method that returns the total file size of all files included in
   * the ContentItem
   * @param f starting file
   * @return long number of bytes
   */
  private long dive(File f){
    long count = 0;
    if(!f.canRead()){
      count = 0;
    }
    else if(f.isFile()){
      return f.length();
    }else{//f.isFolder() 
      count += f.length();
      File files[] = f.listFiles();
      for(int i=0;i<files.length;i++){
        if(files[i].isFile()){
          count += files[i].length();
        }else /*if(files[i].isDirectory())*/ {
          count += dive(files[i]);
        }
      }
    }
    return count;
    //if File isDirectory then Dive(File new);
    //if File isFile then count += size 
  }
  
  /**
   * Recursivley builds arraylist of names of all included files
   * @param f File location
   * @param bc "breadcrumb" Folder depth string, initially empty
   */
  private void makeNames(File f, String bc){
    File files[] = f.listFiles();
    for(int i=0;i<files.length;i++){
      names.add( bc + File.separator + files[i].getName() );
      //every file/folder, etc.  item gets copied, folder get delved into 
      if(files[i].isDirectory()){
        makeNames(files[i], bc + File.separator + files[i].getName());
      }
    }
  }
  
  /**
   * Tests item to see if valid Dove ContentItem
   * @return true if both hasData and hasImage, sets hasData, hasImage, imgType
         returns false if not directory, or cannot read, or no Data or image
   */
  private boolean testData(){
    //
    if(item.isDirectory() && item.canRead() ){
      //boolean data = false;
      //boolean thumb = false;
      String t[] = item.list();
      for(int i=0;i<t.length;i++){
        // checks info.dat for proper info
        if(t[i].equals("info.dat") ){
          hasData = true;
        } // checks for thumb.ext file 
        if(t[i].startsWith("thumb.")){
        /*if(t[i].equals("thumb.png") || t[i].equals("thumb.jpg") 
            || t[i].equals("thumb.gif") ){*/
          int len = t[i].lastIndexOf(".")+1; //the sixth position
          imgType = t[i].substring(len, t[i].length());
          hasImage = true;
          imgSize = item.listFiles()[i].length();
          //hasImage = true;
        }
      }
      return (hasData);
    }else{
      return false;
    }
  }

  /**
   * Reads from info.dat file, a necessary file to be valid ContentItem. 
   */
  private void makeInfo(){
    try {
      String  pull = (item.getAbsolutePath() + File.separator + "info.dat");
      //System.out.println(pull.toString());
      FileInputStream in = new FileInputStream(pull);
      ObjectInputStream inFile = new ObjectInputStream(in);
      info = (Info) inFile.readObject();
      date =  info.getDate();
      inFile.close();
    } catch (IOException e) {
      System.out.println("Info not verified before searching!!");
      e.printStackTrace();
    } catch (ClassNotFoundException e){
      System.out.println("Can't Find Info.class!");
      e.printStackTrace();
    }
  }

  /**
   * Returns ContentItem file location(folder) as File object.
   * @returns File location
   */
  public File getFile() {
    return item;
  }

  /**
   * Returns total file size in bytes
   * @returns total Filesize
   */
  public long getSize() {
    return size;
  }
  
  /**
   * Returns the exact time ContentItem was created as a Date Object.
   * @returns creation Date
   */
  public Date getDate() {
    return date;
  }

  /**
   * Returns Info object that holds more data about the ContentItem
   * @returns Info object
   */
  public Info getInfo() {
    return info;
  }
  
  /**
   * Returns the image thumbnail
   * @returns thumbnail image
   */
  public Image getImage(){
    return img;
  }
  
  /**
   * Returns the image thumbnail file
   * @returns thumbnail image file
   */
  public File getImageFile(){
    return new File(item.getAbsoluteFile() +File.separator+ "thumb." + imgType);
  }
  
  /**
   * Returns the file size of the image thumbnail 
   * @returns thumbnail image filesize
   */
  public long getImageSize(){
    return imgSize;
  }
  
  /**
   * Returns true if ContentItem has a thumbnail image
   * @ reuturns boolean true if there is a thumbnail image
   */
  public boolean hasImage(){
    return hasImage;
  }
  
  /**
   * Returns arraylist of filenames in the ContentItem folder
   * @returns String-type ArrayList of all included file names
   */
  public ArrayList<String>  getNames(){
    return names;
  }
  
  /**
   * Returns a scaled instance of thumbnail image, using a fast but lossy 
   * conversion.
   * @param width desired width
   * @param height desired height
   */
  public Image getImageScaledFast(int width, int height){
    //TODO add precheck or try/catch to catch img == null
    int h = img.getHeight(null);
    int w = img.getWidth(null);
    if(h > w){
      return img.getScaledInstance(-1, height, Image.SCALE_FAST);
    }if(w > h){
      return img.getScaledInstance(width, -1, Image.SCALE_FAST);
    }else{
      return img.getScaledInstance(width, height, Image.SCALE_FAST);
    }
  }
  //TODO check are these two methods equivalent, combine into overloaded method?
  /**
   * Returns a scaled instance of thumbnail image, using a smooth but slow 
   * conversion.
   * @param width desired width
   * @param height desired height
   */
  public Image getImageScaledSmooth(int width, int height){
    return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
  }
  
  /**
   * Reutns true if this ContentItem has been found to have vaild data
   * @returns boolean true if valid data has been found
   */
  public boolean hasData() {
    return hasData;
  }
  
  /**
   * Returns a string of class object information
   * @returns relevant class information in string object
   */
  @Override
  public String toString(){
    return item.getAbsolutePath() + " Size:" + size + " ContentItem:" + hasData 
        +" "+ date + (hasData ? " Name:"+info.getName():" No Data") + 
        (hasImage ? " Image:" + hasImage +" imgSize:"+
        imgSize : " No image.") ;
  }
  
}