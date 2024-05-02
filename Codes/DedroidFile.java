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

    public static void mkdir(String path) {
        File file = new File(path); //以某路径实例化一个File对象
        file.mkdirs();
    }
    public static void mkdir(File file) {
        file.mkdirs();
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
    public static boolean copy(String srcPath, String destPath) throws IOException {
        if(!exists(srcPath)) return false;
        File srcFile = new File(srcPath);
        if(!exists(destPath)){
            mkdir(destPath);
        }
        File destFile = new File(destPath);

        FileInputStream inStream = new FileInputStream(srcFile);
        FileOutputStream outStream = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = inStream.read(buffer)) > 0) {
            outStream.write(buffer, 0, length);
        }

        inStream.close();
        outStream.close();
        return true;
    }
    
    public static boolean exists(String fileName){
        return new File(fileName).exists();
    }
    public static void del(String path){
        new File(path).delete();
    }
}
