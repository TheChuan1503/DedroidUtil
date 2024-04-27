package tc.dedroid.util;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import android.os.Environment;

public class DedroidFile {
    
    final public static String EXTERN_STO_PATH=Environment.getExternalStorageDirectory().getAbsolutePath();

    public static boolean mkdir(String path) {
        boolean r=false;
        File file = new File(path); //以某路径实例化一个File对象
        if (!file.exists()) {
            r = file.mkdirs();
        }
        return r;
    }
    public static boolean mkdir(File file) {
        boolean r=false;
        if (!file.exists()) {
            r = file.mkdirs();
        }
        return r;
    }
    public static boolean mkfile(String fileName,String defaultContent) {
        boolean r=false;
        File file = new File(fileName); //实例化File对象
        if (!file.exists()) {
            mkdir(file.getParentFile());
            try {
                r = file.createNewFile();
                write(fileName,defaultContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return r;
    }
    public static boolean mkfile(String fileName) {
        return mkfile(fileName,"");
    }
    public static void write(String filepath, String content) throws IOException {
        File file = new File(filepath); //以某路径实例化一个File对象
        if (!file.exists()) {
            mkfile(filepath);
        }
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath))) { 
            bufferedWriter.write(content); 
        } 
    }
    public static String read(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append(System.lineSeparator());
            }
            return contentBuilder.toString().trim();
        }
    }
    public static void copy(File source, File dest) throws IOException {    
        InputStream input = null;    
        OutputStream output = null;    
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);        
            byte[] buf = new byte[1024];        
            int bytesRead;        
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }
    public static boolean exists(String fileName){
        return new File(fileName).exists();
    }
}
