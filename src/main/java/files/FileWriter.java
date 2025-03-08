package files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileWriter {
    void writeFileContent(String filePath, String content, boolean append)
            throws NullPointerException, IOException;

    default void writeResourceFileContent(ResourceGetter resourceGetter, String resourceFilename, String content, boolean append)
            throws NullPointerException, IOException {

        String filePath = resourceGetter.getResourcePath("");
        String resourcePath = resourceGetter.getResourcePath(resourceFilename);

        writeFileContent(filePath + resourcePath, content, append);
    }

    void writeFileContent(File writeFile, String content, boolean append)
            throws NullPointerException, IOException;

    default void writeSrcDataFileContent(DataPathGetter dataPathGetter, String dataFilename, String content, boolean append)
            throws NullPointerException, IOException {

        Path filePath = dataPathGetter.getSrcDataPath(dataFilename);
        writeFileContent(filePath.toString(), content, append);
    }

    default void writeTargetDataFileContent(DataPathGetter dataPathGetter, String dataFilename, String content, boolean append)
            throws NullPointerException, IOException {

        Path filePath = dataPathGetter.getTargetDataPath(dataFilename);
        writeFileContent(filePath.toString(), content, append);
    }

    default void overwriteFileContent(String filePath, String content)
            throws NullPointerException, IOException {
        writeFileContent(filePath, content, false);
    }

    default void overwriteResourceFileContent(ResourceGetter resourceGetter, String resourceFilename, String content)
            throws NullPointerException, IOException {
        writeResourceFileContent(resourceGetter, resourceFilename, content, false);
    }

    default void overwriteSrcDataFileContent(DataPathGetter dataPathGetter, String resourceFilename, String content)
            throws NullPointerException, IOException {
        writeSrcDataFileContent(dataPathGetter, resourceFilename, content, false);
    }

    default void overwriteTargetDataFileContent(DataPathGetter dataPathGetter, String resourceFilename, String content)
            throws NullPointerException, IOException {
        writeTargetDataFileContent(dataPathGetter, resourceFilename, content, false);
    }

    default void appendFileContent(String filePath, String content)
            throws NullPointerException, IOException {
        writeFileContent(filePath, content, true);
    }

    default void appendResourceFileContent(ResourceGetter resourceGetter, String resourceFilename, String content)
            throws NullPointerException, IOException {
        writeResourceFileContent(resourceGetter, resourceFilename, content, true);
    }

    default void appendSrcDataFileContent(DataPathGetter dataPathGetter, String dataFilename, String content)
            throws NullPointerException, IOException {
        writeSrcDataFileContent(dataPathGetter, dataFilename, content, true);
    }

    default void appendTargetDataFileContent(DataPathGetter dataPathGetter, String dataFilename, String content)
            throws NullPointerException, IOException {
        writeTargetDataFileContent(dataPathGetter, dataFilename, content, true);
    }
}
