import java.io.IOException;
import java.io.File;
import java.util.StringTokenizer;


public class Tester {
  public static void main(String[] args) throws IOException{
    /*
    Dove one = new Dove();
    one.setSource("/home/jaw/bin/content2/");
    one.getDevices().addDevice("/tmp/ramdisk", "Ramdisk", "16M");
    //System.out.println(one.toString());
    int count = one.getSource().getLength();
    for(int i=0;i<count;i++){
      System.out.println(one.getSource().getInfoAt(i).getName() +"\t"+
          one.getSource().getItemAt(i).hasImage() +"\t"
          /*one.getSource().getItemAt(i).getImage().toString() */ //);
    //}*/
    
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
    //one.getSource().search("an");
    //System.out.println("Searched for 'an' \n" + one.getSource().toString() );
    //one.getSource().refresh();
    
    //one.getSource().search("forever");
    //System.out.println("Searched for 'forever' \n" + one.getSource().toString() );
    //one.getSource().refresh();
    
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
    //File f = new File("/home/jaw/bin/Dove/content2/fake1");
    //System.out.println(f.isDirectory());
    //ContentItem first = new ContentItem(f);
    //System.out.println(first.toString());
    
    //Pass file as args test
    //testFileArgs(args[0]);
    
    //Find files in user home folder
    
    String given = new String("/dev/sr0: LABEL=\"MB SUPPORT CD\" TYPE=\"iso9660\"");
    StringTokenizer tok = new StringTokenizer(given, " :", false );
    // need /dev/BLAH, LABEL=BLAH
    while(tok.hasMoreTokens() ){
      String temp = tok.nextToken();
      //System.out.print(temp+ "  ");
      //System.out.println(temp.indexOf('"') +":"+ temp.lastIndexOf('"'));
      while(temp.contains("\"") && temp.indexOf('"') == temp.lastIndexOf('"')){
        temp = temp +" "+ tok.nextToken();
      }
      if(temp.startsWith("/dev/") ){
        System.out.println(temp);
      }else if(temp.startsWith("LABEL=") ){
        System.out.println(temp.substring(6));
      }else{
      }
    }
    
  }
  public static void testFileArgs(String file){
    File img = new File(file);
    System.out.println(file +"\n"+ img.getPath());
    //works fine, 
  }
}
