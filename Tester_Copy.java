

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
            int c;
            byte[] buffer = new byte[1024];

            while ((c = in.read(buffer)) != -1) {
                out.write(buffer, 0, c);
                progress += c;
                //String chunk = new String(buffer, 0, c);
                System.out.println(c +" "+ progress+" of "+ length+"="+
                	(progress * 100 / length)+": ");
                
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            
        }
    }
	
}

