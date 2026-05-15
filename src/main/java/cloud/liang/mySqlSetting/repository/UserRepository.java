package cloud.liang.mySqlSetting.repository;

import cloud.liang.User.User;
import cloud.liang.common.PasswordUtil;
import cloud.liang.mySqlSetting.datasource.DatabasePool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class UserRepository {
    private UserRepository() {
    }

    public static void addUser(String username, String password, String email, String sex) throws SQLException {
        String sql = "INSERT INTO Users(userName, password, sex, email, status) values (?,?,?,?,?)";
        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, PasswordUtil.hashPassword(password));
            statement.setString(3, sex);
            statement.setString(4, email);
            statement.setString(5, User.ACTIVE_STATUS);
            statement.executeUpdate();
        }
    }

    public static User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Users WHERE userName = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
             statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("userId"),
                            resultSet.getString("userName"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            getFirstChar(resultSet.getString("sex")),
                            resultSet.getString("status")
                    );
                }
            }
        }

        return null;
    }

    public static User findUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE userId = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("userId"),
                            resultSet.getString("userName"),
                            resultSet.getString("password"),
                            resultSet.getString("email"),
                            getFirstChar(resultSet.getString("sex")),
                            resultSet.getString("status")
                    );
                }
            }
        }

        return null;
    }

    public static void updateUserPassword(int userId, String hashedPassword) throws SQLException {
        String sql = "UPDATE Users SET password = ? WHERE userId = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hashedPassword);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    public static List<User> listUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT userId, userName, sex, email, status FROM Users ORDER BY userId";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("userId"),
                        resultSet.getString("userName"),
                        null,
                        resultSet.getString("email"),
                        getFirstChar(resultSet.getString("sex")),
                        resultSet.getString("status")
                ));
            }
        }

        return users;
    }

    public static void updateUser(int userId, String username, String email, String sex, String password)
            throws SQLException {
        boolean updatePassword = password != null && !password.isBlank();
        String sql = updatePassword
                ? "UPDATE Users SET userName = ?, email = ?, sex = ?, password = ? WHERE userId = ? AND status = ?"
                : "UPDATE Users SET userName = ?, email = ?, sex = ? WHERE userId = ? AND status = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, email);
            statement.setString(3, sex);

            if (updatePassword) {
                statement.setString(4, PasswordUtil.hashPassword(password));
                statement.setInt(5, userId);
                statement.setString(6, User.ACTIVE_STATUS);
            } else {
                statement.setInt(4, userId);
                statement.setString(5, User.ACTIVE_STATUS);
            }

            statement.executeUpdate();
        }
    }

    public static void cancelUser(int userId) throws SQLException {
        String sql = "UPDATE Users SET status = ? WHERE userId = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, User.CANCELLED_STATUS);
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }

    private static char getFirstChar(String value) {
        if (value == null || value.isEmpty()) {
            return '\0';
        }

        return value.charAt(0);
    }
}
