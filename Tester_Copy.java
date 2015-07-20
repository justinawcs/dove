

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Tester_Copy {
	public static void main(String[] args) throws IOException {
		BufferedInputStream in = null;
        BufferedOutputStream out = null;
        File src = new File("music.mp3");
        File dest = File.createTempFile("out", ".mp3");
        dest.createNewFile();
        

        try {
            in = new BufferedInputStream(new FileInputStream(src));
            out = new BufferedOutputStream(new FileOutputStream(dest));
            long length = src.length();
            long progress = 0L;
            long loops = length / 1024;
            System.out.println("Filesize: "+ length + "(" + 
            		Dove.humanReadableByteCount(length, true) + ")" +
            		"Predicted number of loops: " + loops);
            long loopCount = 0L;
            int c;
            
            byte[] buffer = new byte[1024];
            Date overall = new Date();
            Date segment = overall;

            while ((c = in.read(buffer)) != -1) {
                out.write(buffer, 0, c);
                progress += c;
                //String chunk = new String(buffer, 0, c);
                long counter  = new Date().getTime() - segment.getTime();
                loopCount++;
                try {
                    Thread.sleep(0);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
//                System.out.println(c +" "+ progress+" of "+ length +
//                	"#loops:" + loopCount + 
//                	" time: " + counter);
                if(counter >= 50L){
                	double percent = (progress * 100d / length);
                	String per = String.format("%5.2f", percent);
                	String size= Dove.humanReadableByteCount(progress, true);
                	
                	long hold = (c * loopCount) ; 
                	long rate = (long) (hold / (counter/1000d));
                	float estim =(length - progress) / (float)rate ;
                	System.out.println(per +"% Size: " + size + 
                			" Time(s):" + counter / 1000d + " "+
                			hold +" "+ Dove.humanReadableByteCount(rate, true) +
                			" "+rate+"  "+ estim  );
                	loopCount = 0L;
                	segment = new Date();
                }
                
                
            }
            long end = new Date().getTime() - overall.getTime();
            long fin = (long) (length / (end/1000d));
            System.out.println("Complete time: "+ end/1000d + "  AVG speed: "+
            		Dove.humanReadableByteCount(fin, true ) +"/sec");
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            
        }
        dest.delete();
        /*File[] f =  File.listRoots();
        for(File i : f){
        	System.out.println(i.toString());
        }*/
        
    }
	
}

