package files;

import exceptions.MissingPropertyException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class DefaultPropertiesReader extends DefaultResourceGetter implements PropertiesReader {
    public DefaultPropertiesReader(Class<?> classObject) {
        super(classObject);
    }

    private void checkFieldValue(String fieldName, String fieldValue) throws MissingPropertyException {
        if (fieldValue == null)
            throw new MissingPropertyException("Missing '%s' property.".formatted(fieldName));
    }

    public String getProperty(Properties props, String fieldName)
            throws NullPointerException, MissingPropertyException {
        checkProperties(props);

        String fieldValue = props.getProperty(fieldName);
        checkFieldValue(fieldName, fieldValue);

        return fieldValue;
    }

    public String getProperty(String resourceFilename, String fieldName)
            throws NullPointerException, IOException, MissingPropertyException {
        if (fieldName == null)
            throw new NullPointerException("Properties field name is null.");

        String resourcePath = getResourcePath(resourceFilename);

        Properties props = new Properties();
        props.load(new FileInputStream(resourcePath));

        return getProperty(props, fieldName);
    }

    public Map<String, String> getProperties(Properties props, List<String> propsFieldsName)
            throws NullPointerException, MissingPropertyException {
        checkProperties(props);

        HashMap<String, String> propsFieldsValues = new HashMap<>();

        for (String fieldName : propsFieldsName) {
            if (fieldName == null)
                continue;

            String fieldValue = props.getProperty(fieldName);
            checkFieldValue(fieldName, fieldValue);

            propsFieldsValues.put(fieldName, fieldValue);
        }

        return propsFieldsValues;
    }

    public Map<String, String> getProperties(String resourceFilename, List<String> propsFieldsName)
            throws NullPointerException, IOException, MissingPropertyException {
        if (propsFieldsName == null)
            throw new NullPointerException("Properties fields name list is null.");

        String resourcePath = getResourcePath(resourceFilename);

        Properties props = new Properties();
        props.load(new FileInputStream(resourcePath));

        return getProperties(props, propsFieldsName);
    }
}
