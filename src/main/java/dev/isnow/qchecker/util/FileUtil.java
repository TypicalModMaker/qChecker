package dev.isnow.qchecker.util;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileWriter;

@UtilityClass
public class FileUtil {

    public File create(File f) {
        try {
            if(!f.exists()) {
                f.createNewFile();
            } else {
                f.delete();
                f.createNewFile();
            }
        } catch (Exception e) {
            System.out.println("FAILED TO CREATE THE FILE!");
        }
        return f;
    }
    public void save(String s, File f) {
        try {
            FileWriter fileWriter = new FileWriter(f, true);
            fileWriter.write(s + "\n");
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("FAILED TO SAVE THE FILE!");
        }
    }
}
