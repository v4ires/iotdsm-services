package deserialization;

import java.util.List;

public interface IDeserializer {
    Object readObject();

    List<Object> readArray();

    boolean loadContent(String... args);

    void close();
}
