package pools;

public enum SqlDrivers {
    POSTGRES("postgresql"), MYSQL("mysql");

    private final String driverName;

    SqlDrivers(String driverName) {
        this.driverName = driverName;
    }

    public String getUrlDriverName() {
        return driverName;
    }

    public String getFieldDriverName() {
        return driverName.toUpperCase();
    }
}
