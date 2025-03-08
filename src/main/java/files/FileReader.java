package files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileReader {
    StringBuilder getFileContent(String filePath) throws NullPointerException, IOException;

    default StringBuilder getFileContent(Path filePath) throws NullPointerException, IOException {
        return getFileContent(filePath.toString());
    }

    StringBuilder getFileContent(File file) throws NullPointerException, IOException;

    default StringBuilder getResourceFileContent(ResourceGetter resourceGetter, String resourceFilename)
            throws NullPointerException, IOException {
        String resourcePath = resourceGetter.getResourcePath(resourceFilename);
        return getFileContent(resourcePath);
    }

    default StringBuilder getSrcDataFileContent(DataPathGetter dataPathGetter, String dataFilename)
            throws NullPointerException, IOException {
        Path dataPath = dataPathGetter.getSrcDataPath(dataFilename);
        return getFileContent(dataPath);
    }

    default StringBuilder getTargetDataFileContent(DataPathGetter dataPathGetter, String dataFilename)
            throws NullPointerException, IOException {
        Path dataPath = dataPathGetter.getTargetDataPath(dataFilename);
        return getFileContent(dataPath);
    }
}
