package pools;

public enum DatabasePoolProperties {
    INIT_CONNS("INIT_CONNS"), INCR_CONNS("INCR_CONNS"), MAX_CONNS("MAX_CONNS");

    private final String FIELD_NAME;

    DatabasePoolProperties(String fieldName) {
        FIELD_NAME = fieldName;
    }

    public String getFieldName(DatabaseTags tag) {
        return "%s_%s".formatted(tag.getFieldDatabaseTagName(), FIELD_NAME);
    }
}
