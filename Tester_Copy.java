

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
            System.out.println(loops);
            long loopCount = 0L;
            int c;
            
            byte[] buffer = new byte[1024];
            Date overall = new Date();
            

            while ((c = in.read(buffer)) != -1) {
                out.write(buffer, 0, c);
                progress += c;
                //String chunk = new String(buffer, 0, c);
                long counter  = new Date().getTime() - overall.getTime();
                System.out.println(c +" "+ progress+" of "+ length+"="+
                	(progress * 100 / length)+": " + loopCount++);
                
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            
        }
        File[] f =  File.listRoots();
        for(File i : f){
        	System.out.println(i.toString());
        }
        
    }
	
}

