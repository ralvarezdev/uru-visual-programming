package files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class DefaultResourceGetter implements ResourceGetter {
    protected final Class<?> CLASS;

    public DefaultResourceGetter(Class<?> classObject) {
        CLASS = classObject;
    }

    protected void checkResourceFilename(String resourceFilename) throws NullPointerException {
        if (resourceFilename == null)
            throw new NullPointerException("Resource filename is null.");
    }

    protected void checkProperties(Properties props) throws NullPointerException {
        if (props == null)
            throw new NullPointerException("Properties instance is null...");
    }

    public URL getResource(String resourceFilename) throws NullPointerException, IOException {
        checkResourceFilename(resourceFilename);

        URL resource = CLASS.getResource(resourceFilename);

        if (resource == null)
            throw new FileNotFoundException("Missing %s file.".formatted(resourceFilename));

        return resource;
    }

    public String getResourcePath(String resourceFilename) throws NullPointerException, IOException {
        URL resource = getResource(resourceFilename);
        return resource.getPath();
    }

    public InputStream getResourceAsStream(String resourceFilename) throws NullPointerException, IOException {
        checkResourceFilename(resourceFilename);

        InputStream resource = CLASS.getResourceAsStream(resourceFilename);

        if (resource == null)
            throw new FileNotFoundException("Missing %s file.".formatted(resourceFilename));

        return resource;
    }

    public String getResourceToExternalForm(String resourceFilename) throws NullPointerException, IOException {
        URL resource = getResource(resourceFilename);
        return resource.toExternalForm();
    }
}
