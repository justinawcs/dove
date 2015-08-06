import java.io.*;
//import java.nio.file.*;
import java.util.ArrayList; 
import java.util.Date;

public class Content {
  private File src; // the Source Folder;
  private boolean allowNoThumb = false; // true=ignore folders w/o thumb.ext
  private boolean searchFileNames;
  private ContentItem[] items, master;
  private int maxItems, itemCount; //counts loaded items 
  private Filter filter;
  
  
  public Content(String s,  boolean allowsNoThumbs){
    allowNoThumb = allowsNoThumbs;
    src = new File(s);
    reload();
  }
  public Content(String s){
    src = new File(s);
    reload();
  }
  
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
  public void refresh(){
    items = master; 
  }
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
  
  public void sortAZ(){
    int scan;
    int i;
    int min;
    ContentItem minVal;
    for(scan = 0;scan < items.length-1; scan++){
      min = scan;
      minVal = items[scan];
      for(i = scan; i<items.length;i++){
        if(items[i].getFile().getName().compareTo(minVal.getFile().getName()) < 0){
          minVal = items[i];
          min = i;
        }
      }
      items[min] = items[scan];
      items[scan] = minVal;
    }
  }
  
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
  
  public void reverse(){
    ContentItem l[] = new ContentItem[items.length];
    for(int i=0; i<items.length; i++){
      l[i] = items[items.length-1 - i];
    }
    items = l;
  }
  public void search(String search){
    search(search, searchFileNames);//refers to settings
  }
  public void searchFilenames(String search){
    search(search, true);//forces filename search
  }
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
  
  public boolean seeker(String search, String given){
    if(given.length() < search.length() ){
      return false;
    }else if( given.equalsIgnoreCase(search)){
      return true;
    }
    for(int i = 0; i < given.length() - search.length(); i++ ){
      if(given.toLowerCase().contains(search.toLowerCase() ) ){
        return true;
      }
    }
    return false;
  }
  
  public void tagsAny(boolean vid, boolean aud, boolean mus, 
      boolean doc, boolean pic, boolean other){
    // if ANY tags that are true are in item then add to list
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
        if(tags[t] == true && temp[t] == true ){
          found++;
        }
        if(found == count){
          hold.add( items[i] );
          exit = true;
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
  public File getSrc(){
    return src;
  }
  public ContentItem getItemAt(int i){
    return items[i];
  }
  public File getFileAt(int i){
    return items[i].getFile();
  }
  public long getSizeAt(int i){
    return items[i].getSize();
  }
  public Date getDateAt(int i){
    return items[i].getDate();
  }
  public Info getInfoAt(int i){
    return items[i].getInfo();
  }
  public ArrayList<String> getNamesAt(int i){
    return items[i].getNames();
  }
  public int getLength(){
    return items.length;
  }
  public int getMaxItems() {
    return maxItems;
  }
  public int getItemCount() {
    return itemCount;
  }
  public boolean isAllowNoThumb() {
    return allowNoThumb;
  }
  public void  setAllowNoThumb(boolean b){
    allowNoThumb = b;
  }
  public void setSearchFileNames(boolean b){
    searchFileNames = b;
  }
  public String toString(){
    String temp = src.getAbsolutePath() + "\n";
    for(int i=0;i<items.length;i++){
      temp += i +": " + items[i].getFile().getName() + " - " + items[i].getSize() 
          +" "+ items[i].hasData() + ": " +
          /*info[i].toString() +*/ items[i].getDate() + "\n" ;
    }
    return temp; 
  }
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