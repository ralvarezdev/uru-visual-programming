package files;

import exceptions.MissingPropertyException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface PropertiesReader extends ResourceGetter {
    String getProperty(Properties props, String fieldName) throws NullPointerException, MissingPropertyException;

    String getProperty(String resourceFilename, String fieldName)
            throws NullPointerException, IOException, MissingPropertyException;

    Map<String, String> getProperties(Properties props, List<String> propsFieldsName)
            throws NullPointerException, MissingPropertyException;

    Map<String, String> getProperties(String resourceFilename, List<String> propsName)
            throws NullPointerException, IOException, MissingPropertyException;

    default Map<String, String> getProperties(Properties props, String... propsName)
            throws NullPointerException, MissingPropertyException {
        return getProperties(props, Arrays.asList(propsName));
    }

    default Map<String, String> getProperties(String resourceFilename, String... propsName)
            throws NullPointerException, IOException, MissingPropertyException {
        return getProperties(resourceFilename, Arrays.asList(propsName));
    }
}
