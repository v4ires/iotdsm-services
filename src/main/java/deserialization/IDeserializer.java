package deserialization;

import java.util.List;

/**
 * University of São Paulo
 * IoT Repository Module
 * @author Vinícius Aires Barros <viniciusaires7@gmail.com>
 */
public interface IDeserializer {

    Object readObject();

    List<Object> readArray();

    boolean loadContent(String... args);

    void close();
}
