package files;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface ResourceGetter {
    URL getResource(String resourceFilename) throws NullPointerException, IOException;

    String getResourcePath(String resourceFilename) throws NullPointerException, IOException;

    String getResourceToExternalForm(String resourceFilename) throws NullPointerException, IOException;

    InputStream getResourceAsStream(String resourceFilename) throws NullPointerException, IOException;
}
