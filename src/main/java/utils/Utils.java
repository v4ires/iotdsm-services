package utils;

import com.google.common.base.Stopwatch;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Utils {

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
     * Print statistics on Stopwatch-reported times for provided number of loops.
     *
     * @param stopwatch   Stopwatch instance with time used statistics.
     */
    public static void printElapsedTime(Stopwatch stopwatch) {
        if (stopwatch.isRunning()) {
            System.out.println("WARNING: Your stopwatch is still running!");
        } else {
            System.out.println("Thread-id: " + Thread.currentThread().getId());
            System.out.println(stopwatch.toString(6));
            System.out.println(stopwatch.elapsedMillis() + " elapsed milliseconds.");
            System.out.println(stopwatch.elapsedTime(TimeUnit.MINUTES) + " minutes");
            System.out.println(stopwatch.elapsedTime(TimeUnit.SECONDS) + " seconds");
            System.out.println(stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + " milliseconds");
            System.out.println(stopwatch.elapsedTime(TimeUnit.NANOSECONDS) + " nanoseconds");
        }
    }

    /**
     *
     * @param start
     * @param end
     */
    public static void printElapsedTime(long start, long end) {
        long ms = (end - start);
        long seconds = (ms / 1000) % 60;
        long minutes = (ms / 60000) % 60;
        long hours = (ms / 3600000);
        System.out.println("Thread-id: " + Thread.currentThread().getId());
        System.out.printf("Time: %d ms or %d:%d:%d\n", ms, hours, minutes, seconds);
    }
}
