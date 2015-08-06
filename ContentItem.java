import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ContentItem{
  
  private File item; //Content Folder 
  private long size; //Total file size
  private Date date; //Creation Date
  private Info info; //Given info from data file
  private boolean hasData, hasImage; 
  private Image img; //thumbnail image
  private String imgType = ""; //File extension of thumbnail
  private long imgSize;
  private ArrayList<String> names = new ArrayList<String>();
  
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
        System.out.print("[ContentItem] Fetching img: " +
            item.getAbsolutePath() );
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
      if(files == null /*|| files.length == 0 */){
        System.out.println("EmptyFolder: " + f );
      }else{
        for(int i=0;i<files.length;i++){
          if(files[i].isFile()){
            count += files[i].length();
          }else /*if(files[i].isDirectory())*/ {
            count += dive(files[i]);
          }
        }
      }
    }
    return count;
    //if File isDirectory then Dive(File new);
    //if File isFile then count += size 
  }
  // build arraylist of names of files
  
  private void makeNames(File f, String bc){
    File files[] = f.listFiles();
    if(files == null /*|| files.length == 0 */){
      System.out.println("EmptyFolder: " + f );
    }else{
      for(int i=0;i<files.length;i++){
        names.add( bc + File.separator + files[i].getName() );
        //every file/folder, etc.  item gets copied, folder get delved into 
        if(files[i].isDirectory()){
          makeNames(files[i], bc + File.separator + files[i].getName());
        }
      }
    }
  }
  
  
  private boolean testData(){
    //Returns true if both hasData and hasImage, sets hasData, hasImage, imgType
    //Returns false if not directory, or can read, or no Data or image
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
  }//-----

  public File getFile() {
    return item;
  }

  public long getSize() {
    return size;
  }

  public Date getDate() {
    return date;
  }

  public Info getInfo() {
    return info;
  }
  
  public Image getImage(){
    return img;
  }
  public File getImageFile(){
    return new File(item.getAbsoluteFile() +File.separator+ "thumb." + imgType);
  }
  public long getImageSize(){
    return imgSize;
  }
  public boolean hasImage(){
    return hasImage;
  }
  public ArrayList<String>  getNames(){
    return names;
  }
  public Image getImageScaledFast(int width, int height){
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
  public Image getImageScaledSmooth(int width, int height){
    return img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
  }
  public boolean hasData() {
    return hasData;
  }
  
  public String toString(){
    return item.getAbsolutePath() + " Size:" + size + " ContentItem:" + hasData 
      +" "+ date + (hasData ? " Name:"+info.getName():" No Data") +(hasImage ? " Image:" + hasImage +" imgSize:"+
      imgSize : " No image.") ;
  }
  
}