package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    /**
     * @param classs
     * @param ann
     */
    public static Set<Field> findFields(Class<?> classs, Class<? extends Annotation> ann) {
        Set<Field> set = new HashSet<>();
        Class<?> c = classs;
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(ann)) {
                    set.add(field);
                }
            }
            c = c.getSuperclass();
        }
        return set;
    }

    /**
     * @param start
     * @param end
     */
    public static String printElapsedTime(long start, long end) {
        long ms = (end - start);
        long seconds = (ms / 1000) % 60;
        long minutes = (ms / 60000) % 60;
        long hours = (ms / 3600000);
        return String.format("time: %d ms or %d:%d:%d", ms, hours, minutes, seconds);
    }
}
