package utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private static Properties props;

    public static void initialize(String fileName) {
        props = new Properties();
        try {
            InputStream in = new FileInputStream(fileName);
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return props.getProperty(key);
    }
}