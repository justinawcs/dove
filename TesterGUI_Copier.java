import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;


public class TesterGUI_Copier extends JFrame {
  
  
  public TesterGUI_Copier() throws IOException{
    File src = new File("music.mp3");
    File dest = File.createTempFile("out", ".mp3");
  }
  
  
  public static void main(String[] args){
    try{
      new TesterGUI_Copier();
    }catch(IOException e){
      e.printStackTrace();
    }
  }
}
