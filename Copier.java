import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;


public class Copier extends SwingWorker<Boolean, Long> {
  ArrayList<ContentItem> list;
  long listTotalSize;
  //File source;
  Devices drive;
  JProgressBar progressBar;
  JLabel statusLabel;
  long bytesCopied = 0L;
  private long averageRate = -1L;
  private float estimateTimeRemaining = -1F;
  private static int BUFFER_LENGTH = 1024;
  private static long TIMER_LENGTH = 500;
  
  public Copier(ArrayList<ContentItem> list, 
      long listTotalSize, 
      Devices drv, 
      JProgressBar bar,
      JLabel status){
    this.list = list;
    this.listTotalSize = listTotalSize;
    //source = src.getFile();
    drive = drv;
    progressBar = bar;
    statusLabel = status;
  }
  
  private void copy(ContentItem item){
//  check for folder name on drive and rename if already there
    File source = item.getFile();
    File check = new File(drive.getMountedDrive().getDoveFile()
        .getAbsolutePath() + File.separator + source.getName());
    int i=1;
    while (check.exists()){
      //Folder is already there, pick new folder name 
      check = new File(drive.getMountedDrive().getDoveFile()
          .getAbsolutePath() + File.separator + 
          source.getName() + "("+ i++ +")");
    }
    check.mkdir();
    String temp = check.toString();
    //set bytesCopied to initial root before copying,
    //its already been been recreated/copied 
    bytesCopied = source.length(); 
    System.out.println("Folder size: " + bytesCopied);
    try{
      dive(source, temp);
    }catch(IOException io){
      // Copy Failed?
    }
  }
  
  private void dive(File f, String bc) throws IOException{
    File files[] = f.listFiles();
    for(int i=0; i<files.length; i++){
      File target = new File(bc + File.separator + files[i].getName() );
      target.setWritable(true);
      if(files[i].isDirectory() ){ // Folder: Chunkcopy cannot copy folders
        Files.copy(files[i].toPath(), target.toPath() );
        bytesCopied += files[i].length();
        dive(files[i], bc + File.separator + files[i].getName());
      }else{ // isFile: Chunk them files
        chunkCopy(files[i], target);
      }
      System.out.print(files[i].toPath() + " : " );
      System.out.println(new File(bc + File.separator + 
          files[i].getName()).toPath() +" "+ files[i].length() );
      if(files[i].isDirectory()){
        
      }
    }
  }
  
  private void chunkCopy(File src, File tgt) throws IOException {
    BufferedInputStream in = null;
    BufferedOutputStream out = null;
    try{
      in = new BufferedInputStream(new FileInputStream(src));
      out = new BufferedOutputStream(new FileOutputStream(tgt));
      int bit;
      byte[] buffer = new byte[BUFFER_LENGTH];
        long length = src.length();
        long progress = 0L;
        long lastRate = -1L;
        long loopCount = 0L;
        Date start = new Date();
        Date segmentTime = start;
      //sets bit = read buffer, and checks that the file still has data left
      while((bit = in.read(buffer)) != -1){
        //writes the buffer to target 
        out.write(buffer, 0, bit);
        bytesCopied += bit;
        loopCount++;
        long timer = new Date().getTime() - segmentTime.getTime();
        if(timer >= TIMER_LENGTH){
          //'seg' = segment, a grouping of these loops
          long segDataSize = bit * loopCount;
          long segDataRate = (long) (segDataSize / (timer/1000d));//millisecond
          //zero pre-check, sets pastRate=rate if -1, or first run
          lastRate = (lastRate == -1L) ? segDataRate : lastRate;
          long avgRate = (lastRate + segDataRate) / 2; //averageRate
          float estTimeRem = (length - progress) / (float)avgRate;
          //TODO all length??
          averageRate = avgRate;
          estimateTimeRemaining = estTimeRem;
          this.publish(bytesCopied);
          lastRate = segDataRate;
          System.out.println(src+" AvgRate:"+avgRate +" TimeRem"+ estTimeRem);
          loopCount = 0L;
          segmentTime = new Date();
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    finally{
      in.close();
      out.close();
    }
  }
  
  @Override
  protected Boolean doInBackground() {
    System.out.println("Starting copy.");
    for(int i=0; i<list.size(); i++){
      try{
        if(!drive.getMountedDrive().isSetup()){
          drive.getMountedDrive().setupDrive();
        }
        Thread.sleep(0000);
        copy(list.get(i));
        //publish() called from copyChunks
      }catch(InterruptedException e){
        System.out.println("Thread refused to sleep...");
      }
    }
    return true;
  }
  
  //@Override
  protected void process(Long bytesCopied){
    super.process(null);
    int progress = (int) (bytesCopied / listTotalSize);
    progressBar.setValue(progress);
    progressBar.setString(progress + "%");
    String avgRate = Dove.humanReadableByteCount(bytesCopied, true) + "/s";
    statusLabel.setText("Average Rate: " + avgRate+ "\nTime Remaining: "+
        estimateTimeRemaining + "s");
    System.out.println(statusLabel.getText());
  }
  
  @Override
  protected void done(){
    try {
      get();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    progressBar.setString("Finished.");
    System.out.println("Done. Copied: "+ bytesCopied +" ListSize: " +
        listTotalSize ); 
    //bRemoveDevice.setEnabled(true);
  }
  
  private class CopyStatus{
    public CopyStatus(long bytesCopied, long averageRate, float estTimeRem){
      
    }
  }
  
}


