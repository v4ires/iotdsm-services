package deserialization;

import java.util.List;

/**
 * University of Sao Paulo
 * IoT Repository Module
 *
 * @author Vinicius Aires Barros <viniciusaires@usp.br>
 */
public interface IDeserializer {

    Object readObject();

    List<Object> readArray();

    boolean loadContent(String... args);

    void close();
}
