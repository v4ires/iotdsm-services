package model;

public enum GlobalVariable {

    HOST("HOST"),
    PORT("PORT"),
    DATABASE("DATABASE"),
    USER("USER"),
    PASSWORD("PASSWORD"),
    DRIVER("DRIVER"),
    SQL_DEBUG("SQL_DEBUG"),
    DATABASETYPE("DATABASETYPE"),
    USEHIBERNATE("USEHIBERNATE"),
    SPARK_THREAD_POOL("SPARK_THREAD_POOL"),
    SPARK_THREAD_POOL_TIMEOUT("SPARK_THREAD_POOL_TIMEOUT"),
    SPARK_THREAD_POOL_MIN("SPARK_THREAD_POOL_MIN"),
    SPARK_THREAD_POOL_MAX("SPARK_THREAD_POOL_MAX"),
    APIPORT("APIPORT"),
    DIALECT("DIALECT");

    private String value;

    GlobalVariable(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
