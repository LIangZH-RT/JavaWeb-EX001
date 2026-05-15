package cloud.liang.mySqlSetting.datasource;

import cloud.liang.mySqlSetting.config.DatabaseConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabasePool {
    private static volatile HikariDataSource dataSource;

    private DatabasePool() {
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void close() {
        HikariDataSource currentDataSource = dataSource;
        if (currentDataSource != null) {
            currentDataSource.close();
            dataSource = null;
        }
    }

    private static HikariDataSource getDataSource() {
        HikariDataSource currentDataSource = dataSource;
        if (currentDataSource == null) {
            synchronized (DatabasePool.class) {
                currentDataSource = dataSource;
                if (currentDataSource == null) {
                    currentDataSource = createDataSource();
                    dataSource = currentDataSource;
                }
            }
        }

        return currentDataSource;
    }

    private static HikariDataSource createDataSource() {
        DatabaseConfig.validate();

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(DatabaseConfig.getUrl());
        config.setUsername(DatabaseConfig.getUsername());
        config.setPassword(DatabaseConfig.getPassword());
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("q-system-pool");

        return new HikariDataSource(config);
    }
}
