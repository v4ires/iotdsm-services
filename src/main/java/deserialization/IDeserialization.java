package deserialization;

import java.util.List;

public interface IDeserialization {
    Object readSingle();
    List<Object> readMultiple();
    void loadContent(String... args);
}
