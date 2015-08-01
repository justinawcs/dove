import java.io.*;
//import java.nio.file.*;
import java.util.ArrayList; 
import java.util.Date;

/**
 * Maintains ContentItem objects, loads, sorts, searches, returns info
 * @author Justin A. Williams
 * @version 0.0.8
 */
public class Content {
  private File src; // the Source Folder;
  private boolean allowNoThumb = false; // true=ignore folders w/o thumb.ext
  private boolean searchFileNames;
  private ContentItem[] items;  // ContentItems that are currently displayed
  private ContentItem[] master; // All ContentItems
  private int maxItems, itemCount; //counts loaded items //TODO: check usage
  private Filter filter; //TODO: remove filter
  
  /**
   * Class contructor that allows the setting of allowsNoThumbs to skip 
   * ContentItems without thumbnail imagaes
   * @param s absolute Source folder location given as String
   * @param allowsNoThumbs to use thumbnail-less ContentItems set true
   */
  public Content(String s,  boolean allowsNoThumbs){
    src = new File(s);
    allowNoThumb = allowsNoThumbs;
    reload();
  }
  
  /**
   * Class contructor that takes the Source location as a String 
   * @param s absolute Source folder location given as String
   */
  public Content(String s){
    src = new File(s);
    reload();
  }
  
  /**
   * Reads from Source folder and populates ContentItem arrays (items, master)
   */
  public void reload(){
    File[] temp = src.listFiles();
    maxItems = temp.length;
    itemCount = 0;
    System.out.println("[Content] Loading "+temp.length +
      (temp.length > 1 ? " items." : " item.") 	);
    items = new ContentItem[temp.length];
    for(int i=0; i<temp.length;i++){
      items[i] = new ContentItem(temp[i]);
      itemCount++;
    }
    prune();
    master = items;
  }
  
  /**
   * Resets selective items list to master array, this prevents having to 
   * reload() so often
   */
  public void refresh(){
    items = master; 
  }
  
  /**
   * Removes ContentItems that either have no Dove data, or are do not contain 
   * required thumbnail Images
   * @see ContentItem.hasData()
   * @see allowNoThumb
   */
  private void prune(){
    ArrayList<ContentItem> hold = new ArrayList<ContentItem>(); 
    for(int i=0; i<items.length;i++){
      if(items[i].hasData() &&  (items[i].hasImage() || allowNoThumb) ){
        hold.add( items[i] );
      }
    }
    hold.trimToSize();
    items = new ContentItem[hold.size()];
    for(int j=0;j<items.length;j++){
      items[j] = hold.get(j);
    }
  }
  
  /**
   * Sorts ContentItems in items array alphabetically.
   */
  public void sortAZ(){
    int scan;
    int i;
    int min;
    ContentItem minVal;
    for(scan = 0;scan < items.length-1; scan++){
      min = scan;
      minVal = items[scan];
      for(i = scan; i<items.length;i++){
        if(items[i].getFile().getName().compareTo(
              minVal.getFile().getName()) < 0){
          minVal = items[i];
          min = i;
        }
      }
      items[min] = items[scan];
      items[scan] = minVal;
    }
  }
  
  /**
   * Sorts ContentItems in items array by Date.
   */
  public void sortDate(){
    int scan;
    int i;
    int min;
    ContentItem minDate;
    for(scan = 0;scan <items.length-1; scan++){
      min = scan;
      minDate = items[scan];
      for(i=scan; i<items.length; i++){
        if(items[i].getDate().before(minDate.getDate()) ){
          minDate = items[i];
          min = i;
        }
      }
      items[min] = items[scan];
      items[scan] = minDate;
    }
  }
  
  /**
   * Sorts ContentItems in items array by sizes.
   */
  public void sortSize(){
    int scan;
    int i;
    int min;
    ContentItem minVal;
    for(scan = 0;scan < items.length-1; scan++){
      min = scan;
      minVal = items[scan];
      for(i = scan; i<items.length;i++){
        if(items[i].getSize() < minVal.getSize()){
          minVal = items[i];
          min = i;
        }
      }
      items[min] = items[scan];
      items[scan] = minVal;
    }
  }
  
  /**
   * Sorts ContentItems in items array by tags.
   */
  public void sortTags(){
    int scan;
    int i;
    int min;
    ContentItem minType;
    for(scan=0; scan < items.length-1; scan++){
      min = scan;
      minType = items[scan];
      for(i=scan; i<items.length; i++){
        if(items[i].getInfo().getTagSum() < minType.getInfo().getTagSum() ){
          minType = items[i];
          min = i;
        }
      }
      items[min] = items[scan];
      items[scan] = minType;
    }
  }
  
  /**
   * Reverses the current sorts of ContentItems in items array.
   */
  public void reverse(){
    ContentItem l[] = new ContentItem[items.length];
    for(int i=0; i<items.length; i++){
      l[i] = items[items.length-1 - i];
    }
    items = l;
  }
  
  /**
   * Searches for given string in info of ContentItems.
   * @param search will look for this exact sequence of letters
   */
  public void search(String search){
    search(search, searchFileNames);//refers to settings
  }
  
  /**
   * Searches for given string in info and possibly filenames of ContentItems
   * @param search will look for this exact sequence of letters
   * @param searchFileNames true searches filenames also, takes longer
   */
  public void search(String search, boolean searchFileNames){
    search = search.trim();
    // if search is in any part of given then add to list
    ArrayList<ContentItem> hold = new ArrayList<ContentItem>(); 
    for(int i=0; i<items.length;i++){
      Info temp = items[i].getInfo();
      if( seeker(search, temp.getName()) || seeker(search, temp.getDesc())
          || seeker(search, temp.getOrigin()) ){
        hold.add( items[i] );
      }else if(searchFileNames == true){
        if( searchDive(search, items[i].getFile()) ){
          hold.add(items[i]);
        }
      }
    }
    hold.trimToSize();
    items = new ContentItem[hold.size()];
    for(int j=0;j<items.length;j++){
      items[j] = hold.get(j);
    }
  }
  
  /**
   * Searches for given string in info and filenames of ContentItems
   * @param search will look for this exact sequence of letters
   */
  public void searchFilenames(String search){
    search(search, true);//forces filename search
  }
  
  /**
   * Recursive search function looking for exact string in filenames of given 
   * folder location. Traverses files  in all folder below until found.
   * @param search will look for this exact sequence of letters
   * @param f folder to be searched for string
   * @returns true if search string is found
   */
  private boolean searchDive(String search, File f){
    //Searches the filenames in items[], recursively,    
    search = search.trim();
    if(seeker(search, f.getName()) ){
      return true;
    }else if(f.isDirectory()){ //filename not found, but if folder look inside
      File folder[] = f.listFiles();
      for(int i=0; i<folder.length; i++){
        if(searchDive(search, folder[i])){
          return true;
        }else{//just loop through!
        }
      }//loop search failed
      return false;
    }else{ //not in file/folder name, not in folder
      return false;
    }
  }
  
  /**
   * Searches for string sequnece in given string, 
   * @param search  will look for this exact sequence of letters
   * @param given will be searched for 'search'
   * @return true if search is found in given
   */
  public boolean seeker(String search, String given){
    if(given.length() < search.length() ){
      return false;
    }else if( given.equalsIgnoreCase(search)){
      return true;
    }//TODO check code here, is for loop necessary?
    for(int i = 0; i < given.length() - search.length(); i++ ){
      if(given.toLowerCase().contains(search.toLowerCase() ) ){
        return true;
      }
    }
    return false;
  }
  
  /**
   * Using the given tags, any ContentItems that matches with any of the given 
   * tag will be added to items array. Non matching will be ignored, leaving 
   * only matches.
   * @deprecated non-intuitive, invoke an arbitray user choice
   * @see tags(...)
   * @param vid Contains Video
   * @param aud Contains Audio
   * @param mus Contains Music
   * @param doc Contains Documents
   * @param pic Contains Pictures
   * @param other Contains Other data
   */
  @Deprecated
  public void tagsAny(boolean vid, boolean aud, boolean mus, 
      boolean doc, boolean pic, boolean other){
    //
    ArrayList<ContentItem> hold = new ArrayList<ContentItem>(); 
    boolean tags[] = {vid, aud, mus, doc, pic, other};
    
    for(int i=0; i<items.length;i++){
      boolean[] temp = items[i].getInfo().getTags();
      boolean found = false;
      int t=0;
      while(!found && t<tags.length){
        //for(int t=0; t<tags.length; t++){
        if(tags[t] == true && temp[t] == true ){
          hold.add( items[i] );
          found = true;
        }
        t++;
      }
    }
    hold.trimToSize();
    items = new ContentItem[hold.size()];
    for(int j=0;j<items.length;j++){
      items[j] = hold.get(j);
    }
  }

  /**
   * Using the given tags, any ContentItems that matches with all of the given 
   * tag will be added to items array. Non matching will be ignored, leaving 
   * only matches.
   * @deprecated non-intuitive, invoke an arbitray user choice
   * @see tags(...)
   * @param vid Contains Video
   * @param aud Contains Audio
   * @param mus Contains Music
   * @param doc Contains Documents
   * @param pic Contains Pictures
   * @param other Contains Other data
   */
  @Deprecated
  public void tagsAll(boolean vid, boolean aud, boolean mus, 
      boolean doc, boolean pic, boolean other){
    // if ALL tags that are true are in item then add to list
    ArrayList<ContentItem> hold = new ArrayList<ContentItem>(); 
    boolean tags[] = {vid, aud, mus, doc, pic, other};
    
    for(int i=0; i<items.length;i++){
      boolean[] temp = items[i].getInfo().getTags();
      int match = 0;
      for(int t=0; t<tags.length; t++){
        if(tags[t] == temp[t]){
          match++;
        }
      }
      if(match==tags.length){//all tags match
        hold.add( items[i] );
      }
    }
    hold.trimToSize();
    items = new ContentItem[hold.size()];
    for(int j=0;j<items.length;j++){
      items[j] = hold.get(j);
    }
  }
  
  /**
   * Using the given tags, any ContentItems that matches with any of the given 
   * tag will be added to items array. Non matching will be ignored, leaving 
   * only matches. 
   * @param vid Contains Video
   * @param aud Contains Audio
   * @param mus Contains Music
   * @param doc Contains Documents
   * @param pic Contains Pictures
   * @param other Contains Other data
   */
  public void tags(boolean vid, boolean aud, boolean mus, 
      boolean doc, boolean pic, boolean other){
    //
    ArrayList<ContentItem> hold = new ArrayList<ContentItem>(); 
    boolean tags[] = {vid, aud, mus, doc, pic, other};
    int count = 0;
    //count number of given tags
    for(int i=0;i<tags.length;i++){
      count += (tags[i]) ? 1 : 0;
    }
    for(int i=0; i<items.length;i++){
      boolean[] temp = items[i].getInfo().getTags();
      boolean exit = false;
      int found = 0;
      int t=0;
      while(!exit && t<tags.length){
        //for(int t=0; t<tags.length; t++){
        //count number of matching tags
        if(tags[t] == true && temp[t] == true ){
          found++;
        }
        //when tag count matches, add to list
        if(found == count){
          hold.add( items[i] );
          exit = true;
        }
        t++;
      }
    }
    //load hold into items array
    hold.trimToSize();
    items = new ContentItem[hold.size()];
    for(int j=0;j<items.length;j++){
      items[j] = hold.get(j);
    }
  }
  
  /**
   * Items array will only contain ContentItems that have thumbnail images.
   */
  public void onlyThumbs(){
    ArrayList<ContentItem> hold = new ArrayList<ContentItem>(items.length); 
    for(int i=0; i<items.length;i++){
      //ContentItem temp = items[i].getInfo();
      if( items[i].hasImage() ){
        hold.add( items[i] );
      }
    }
    hold.trimToSize();
    items = new ContentItem[hold.size()];
    for(int j=0;j<items.length;j++){
      items[j] = hold.get(j);
    }
  }
  
  /**
   * Returns Content folder File object 
   * @returns Content File folder
   */
  public File getSrc(){
    return src;
  }
  
  /**
   * Returns ContentItem at given items array index
   * @param i index
   * @returns given ContentItem
   */
  public ContentItem getItemAt(int i){
    return items[i];
  }
  
  /**
   * Returns ContentItem File object at given items array index
   * @param i index
   * @returns given ContentItem File Object
   */
  public File getFileAt(int i){
    return items[i].getFile();
  }
  
  /**
   * Returns ContentItem total filesize at given items arrat index
   * @param i index
   * @returns Long filesize
   */
  public long getSizeAt(int i){
    return items[i].getSize();
  }
  
  /**
   * Returns ContentItem date of creation at given items array index
   * @param i index
   * @returns Date object
   */
  public Date getDateAt(int i){
    return items[i].getDate();
  }
  
  /**
   * Returns ContentItem Info object at given items array index
   * @param i index
   * @returns Info object
   */
  public Info getInfoAt(int i){
    return items[i].getInfo();
  }
  
  /**
   * Returns String-Type ArrayList of all filenames within a given items array
   * index
   * @param i index
   * @returns String type ArrayList
   */
  public ArrayList<String> getNamesAt(int i){
    return items[i].getNames();
  }
  
  /**
   * Returns number of items currently in items array
   * @returns integer
   */
  public int getLength(){
    return items.length;
  }
  
  /**
   * Returns number of all ContentItems
   * @returns int
   */
  public int getMaxItems() {
    return maxItems;
  }
  
  /**
   * Returns number of displayed ContentItems
   * @returns int
   */
  public int getItemCount() {
    return itemCount;
  }
  
  /**
   * Returns true if ContentItems without thumbnail images will be displayed, 
   * else false
   * @returns boolean
   */
  public boolean isAllowNoThumb() {
    return allowNoThumb;
  }
  
  /**
   * Sets thumbnail image policy, true if ContentItems without thumbs will be 
   * allows, else false
   */
  public void  setAllowNoThumb(boolean b){
    allowNoThumb = b;
  }
  
  /**
   * Set search filenames policy, true allows searching filenames in addition 
   * to info data. Searches all files names in ContentItem.
   */
  public void setSearchFileNames(boolean b){
    searchFileNames = b;
  }
  
  /**
   * Returns string of relevant Class data
   */
  public String toString(){
    String temp = src.getAbsolutePath() + "\n";
    for(int i=0;i<items.length;i++){
      temp += i +": " + items[i].getFile().getName() + " - " + items[i].getSize() 
          +" "+ items[i].hasData() + ": " +
          /*info[i].toString() +*/ items[i].getDate() + "\n" ;
    }
    return temp; 
  }
  
  //TODO: Check usage, remove?
  private class Filter{
    //boolean isSearch;
    String search;
    //boolean isType;
    Boolean[] tags;
    boolean isThumbsOnly;
    
    public Filter(){
      //boolean tags[] = {vid, aud, mus, doc, pic, other};
      search.equals(null);
      Boolean isType;
      //isType
      isType = false;
      isThumbsOnly = false;
    }
    public void setSearch(String search){
      //isSearch = true;
      //sear
    }
  }
}