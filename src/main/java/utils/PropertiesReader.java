package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private Properties props;

    public PropertiesReader(String fileName) {
        props = new Properties();
        try {
            InputStream in = new FileInputStream(fileName);

            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getValue(String key) {
        return props.getProperty(key);
    }

}