package files;

import java.nio.file.Path;

public interface DataPathGetter {
    Path getSrcDataPath(String dataFilename) throws NullPointerException;

    Path getSrcDataPath() throws NullPointerException;

    Path getTargetDataPath(String dataFilename) throws NullPointerException;

    Path getTargetDataPath() throws NullPointerException;
}
