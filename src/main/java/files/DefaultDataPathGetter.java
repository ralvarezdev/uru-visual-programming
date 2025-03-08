package files;

import util.OS;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultDataPathGetter implements DataPathGetter {
    private final Path SRC_DATA_PATH;
    private final Path TARGET_DATA_PATH;

    public DefaultDataPathGetter() throws IOException {
        DefaultResourceGetter resourcePathGetter = new DefaultResourceGetter(DefaultDataPathGetter.class);

        String resourcePath = resourcePathGetter.getResourcePath("");
        Path CURR_PATH = Path.of((OS.getOS() == OS.Windows) ? resourcePath.substring(3) : resourcePath);

        Path ROOT_PATH = CURR_PATH.getParent().getParent().getParent().getParent();
        Path DATA_PATH = Paths.get(ROOT_PATH.toString(), "data");
        SRC_DATA_PATH = Paths.get(DATA_PATH.toString(), "src");
        TARGET_DATA_PATH = Paths.get(DATA_PATH.toString(), "target");
    }

    public void checkDataFilename(String dataFilename) throws NullPointerException {
        if (dataFilename == null)
            throw new NullPointerException("Data filename is null.");
    }

    public Path getSrcDataPath(String dataFilename) throws NullPointerException {
        checkDataFilename(dataFilename);

        return Paths.get(SRC_DATA_PATH.toString(), dataFilename);
    }

    public Path getTargetDataPath(String dataFilename) throws NullPointerException {
        checkDataFilename(dataFilename);

        return Paths.get(TARGET_DATA_PATH.toString(), dataFilename);
    }

    public Path getSrcDataPath() throws NullPointerException {
        return Paths.get(SRC_DATA_PATH.toString());
    }

    public Path getTargetDataPath() throws NullPointerException {
        return Paths.get(TARGET_DATA_PATH.toString());
    }
}
