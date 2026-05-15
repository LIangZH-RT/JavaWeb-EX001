package cloud.liang.mySqlSetting;

import cloud.liang.mySqlSetting.datasource.DatabasePool;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DatabaseInitializerListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection connection = DatabasePool.getConnection()) {
            SqlConnector.createTables(connection);
        } catch (SQLException | RuntimeException e) {
            throw new RuntimeException(
                    "Failed to initialize database schema. Configure Q_SYSTEM_DB_PASSWORD or -Dq.system.db.password.",
                    e
            );
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabasePool.close();
    }
}
