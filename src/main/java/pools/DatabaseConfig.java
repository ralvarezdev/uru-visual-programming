package pools;

public record DatabaseConfig(DatabaseTags tag, String host, String port, String database, String user,
                             String password) {
    public String url() {
        return "jdbc:%s://%s:%s/%s".formatted(tag.getUrlDriverName(), host, port, database);
    }
}
