package cloud.liang.mySqlSetting.config;

public final class DatabaseConfig {
    private static final String LOCAL_URL =
            "jdbc:mysql://localhost:3306/q_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String LOCAL_USERNAME = "";
    private static final String LOCAL_PASSWORD = "";

    private static final String URL = getSetting("Q_SYSTEM_DB_URL", "q.system.db.url", LOCAL_URL);
    private static final String USERNAME = getSetting("Q_SYSTEM_DB_USER", "q.system.db.user", LOCAL_USERNAME);
    private static final String PASSWORD = getSetting("Q_SYSTEM_DB_PASSWORD", "q.system.db.password", LOCAL_PASSWORD);

    private DatabaseConfig() {
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }

    public static void validate() {
        if (PASSWORD.isBlank()) {
            throw new IllegalStateException(
                    "Database password is empty. Set Q_SYSTEM_DB_PASSWORD, -Dq.system.db.password, or fill DatabaseConfig.LOCAL_PASSWORD."
            );
        }
    }

    private static String getSetting(String envName, String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue.trim();
        }

        return defaultValue;
    }
}
