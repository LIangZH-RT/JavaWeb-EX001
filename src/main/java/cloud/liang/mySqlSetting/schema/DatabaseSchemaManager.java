package cloud.liang.mySqlSetting.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseSchemaManager {
    private DatabaseSchemaManager() {
    }

    public static void createTables(Connection connection) throws SQLException {
        String createUserTable =
                "CREATE TABLE IF NOT EXISTS Users(" +
                        "userId int primary key auto_increment, " +
                        "userName varchar(50), " +
                        "password varchar(255), " +
                        "sex char(1), " +
                        "email varchar(50), " +
                        "status varchar(20) not null default 'ACTIVE'" +
                        ")";

        String createQuestionTable =
                "CREATE TABLE IF NOT EXISTS question(" +
                        "questionId int primary key auto_increment, " +
                        "title varchar(50), " +
                        "optionA varchar(20), " +
                        "optionB varchar(20), " +
                        "optionC varchar(20), " +
                        "optionD varchar(20), " +
                        "answer char(1)" +
                        ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createQuestionTable);
            statement.executeUpdate(createUserTable);
            statement.executeUpdate("ALTER TABLE Users MODIFY password varchar(255)");
            addColumnIfMissing(statement, "Users", "status", "varchar(20) not null default 'ACTIVE'");
        }
    }

    private static void addColumnIfMissing(Statement statement, String tableName, String columnName,
                                           String columnDefinition) throws SQLException {
        try {
            statement.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            int errorCode = e.getErrorCode();
            if ("42S21".equals(sqlState) || errorCode == 1060) {
                return;
            }

            throw e;
        }
    }
}
