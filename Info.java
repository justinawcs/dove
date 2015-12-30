import java.io.Serializable;
import java.util.Date;

/**
 * Maintains information from the ContentItem class: name, origin, description,
 * crreation date, and content type tags: video, audio, music, document,
 * picture, other.
 * @author Justin A. Williams
 * @version 0.0.8
 */
public class Info implements Serializable{
  private static final long serialVersionUID = 1L;
  private String name;
  private String origin;
  private String desc;
  private Date born;
  private boolean vid, aud, mus, doc, pic, other;
  
  /**
   * Contstructor: Creates object from given data, requires explict time as Date 
   * object.
   * @param nm name, gives name to Content
   * @param or origin, where Content originated
   * @param ds description, more information about Content
   * @param dt date, date/time Content was created
   * @param v video, contains video
   * @param a audio, contains audio
   * @param m music, contains music
   * @param d document, contains document
   * @param p picture, contains picture
   * @param o other, contains other type of data
   */
  public Info(String nm, String or, String ds, Date dt, boolean v, 
        boolean a, boolean m, boolean d, boolean p, boolean o){
    name = nm;
    origin = or;
    desc = ds;
    born = dt;
    vid = v;
    aud = a;
    mus = m;
    doc = d;
    pic = p;
    other = o;
  }
  
  /**
   * Contstructor: Creates object from given data, time/date is created
   * automatically.
   * @param nm name, gives name to Content
   * @param or origin, where Content originated
   * @param ds description, more information about Content
   * @param v video, contains video
   * @param a audio, contains audio
   * @param m music, contains music
   * @param d document, contains document
   * @param p picture, contains picture
   * @param o other, contains other type of data
   */
  public Info(String nm, String or, String ds, boolean v, 
      boolean a, boolean m, boolean d, boolean p, boolean o){
  name = nm;
  origin = or;
  desc = ds;
  born = new Date();
  vid = v;
  aud = a;
  mus = m;
  doc = d;
  pic = p;
  other = o;
  }
  
  /**
   * Returns name of Content.
   * @returns name String
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns origin of Content.
   * @returns origin String
   */
  public String getOrigin() {
    return origin;
  }
  
  /**
   * Returns description of Content.
   * @returns description String
   */
  public String getDesc() {
    return desc;
  }
  
  /**
   * Returns date of Content creation.
   * @ereturn date object
   */
  public Date getDate() {
    return born;
  }
  
  /**
   * Returns: does Content contain video.
   * @returns video boolean
   */
  public boolean isVideo() {
    return vid;
  }
  
  /**
   * Returns: does Content contain audio.
   * @return audio boolean
   */
  public boolean isAudio() {
    return aud;
  }
  
  /**
   * Returns: does Content contain music.
   * @return music boolean
   */
  public boolean isMusic() {
    return mus;
  }
  
  /**
   * Returns: does Content contain document.
   * @return document boolean
   */
  public boolean isDocument() {
    return doc;
  }
  
  /**
   * Returns: does Content contain picture.
   * @return picture boolean
   */
  public boolean isPictures() {
    return pic;
  }
  
  /**
   * Returns: does Content contain other data type.
   * @return other boolean
   */
  public boolean isOther() {
    return other;
  }
  
  /**
   * Returns boolean array of tags: video, audio, music, document, picture, 
   * other.
   * @returns boolean array
   */
  public boolean[] getTags(){
    boolean[] temp = new boolean[] {vid, aud, mus, doc, pic, other}; 
    return temp;
  }
  
  /**
   * Returns sum of tags used.
   * Example: Audio, Music, and Picture are TRUE,  then 3 is returned.
   * @returns integer sum of tags
   */
  public int getTagSum(){
    int i=0;
    for(boolean b:getTags()){
      i += b ? 1 : 0; 
    }
    return i;
  }
  
  /**
   * Returns a human readable String of tags.
   * @returns String of tags
   */
  public String getTagsString(){
    if(getTagSum()==0){
      return "None.";
    }
    String hold = "";
    hold += vid ? "Video, " : "";
    hold += aud ? "Audio, " : "" ;
    hold += mus ? "Music, " : "" ;
    hold += doc ? "Document, " : "" ;
    hold += pic ? "Pictures, " : "" ;
    hold += other ? "Other, " : "" ;
    //now trim that last comma and add a periond
    String temp = hold.substring(0, hold.length()-2 ) + ".";
    return temp;
  }
  
  /**
   * Returns String of class information.
   * @returns string
   */
  public String toString(){
    return "Name: " + name + "\n" +
        "Origin: " + origin + "\n" + 
        "Description: " + desc + "\n" + 
        "Date: " + born.toString() + "\n" +
        "Video: " + vid + "\n" +
        "Audio: " + aud + "\n" +
        "Music: " + mus + "\n" +
        "Document: " + doc + "\n" +
        "Pictures: " + pic + "\n" +
        "Other: " + other;
  }
  
  /**
   * Returns HTML formated string of class information, takes no parameters.
   * @returns string
   * @Deprecated assumes width of 350px and cannot relay thumbnail
   */
  @Deprecated
  public String toHtml(){
    String h1 = "<!--\n" + toString() + "\n-->\n";  
    h1 += "<html><body style='width:350px' ><table> ";
    h1 += "<tr><td halign='center' colspan='2'><img alt='Thumbnail: N/A' />";
    h1 += "<tr><td valign='baseline'>Name:</td> <td>"+ name +"</td></tr>";
    h1 += "<tr><td valign='baseline'>Origin:</td> <td>"+ origin +"</td></tr>";
    h1 += "<tr><td valign='baseline'>Description:</td> <td>"+ desc +"</td></tr>";
    h1 += "<tr><td valign='baseline'>Date:</td> <td>"+ born.toString() +"</td></tr>";
    h1 += "<tr><td>Media Type:</td> <td>"+ getTagsString() +"</td></tr>";
    h1 += "</table></body></html>";
    return h1;
  }
  
  /**
   * Returns HTML formated string of class information, takes thumb ext.
   * @returns string
   */
   //TODO take HTML width as parameter
  public String toHtml(String thumbExt){
    String h1 = "<!--\n" + toString() + "\n-->\n";  
    h1 += "<html><body style='width:350px' ><table> ";
    h1 += "<tr><td halign='center' colspan='2'><img src='thumb." + thumbExt +" ' />";
    h1 += "<tr><td valign='baseline'>Name:</td> <td>"+ name +"</td></tr>";
    h1 += "<tr><td valign='baseline'>Origin:</td> <td>"+ origin +"</td></tr>";
    h1 += "<tr><td valign='baseline'>Description:</td> <td>"+ desc +"</td></tr>";
    h1 += "<tr><td valign='baseline'>Date:</td> <td>"+ born.toString() +"</td></tr>";
    h1 += "<tr><td>Media Type:</td> <td>"+ getTagsString() +"</td></tr>";
    h1 += "</table></body></html>";
    return h1;
  }
}
