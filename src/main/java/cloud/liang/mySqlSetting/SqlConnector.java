package cloud.liang.mySqlSetting;

import cloud.liang.Question.Questions;
import cloud.liang.User.User;
import cloud.liang.mySqlSetting.datasource.DatabasePool;
import cloud.liang.mySqlSetting.repository.QuestionRepository;
import cloud.liang.mySqlSetting.repository.UserRepository;
import cloud.liang.mySqlSetting.schema.DatabaseSchemaManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class SqlConnector {
    private SqlConnector() {
    }

    public static Connection getSqlConnection() throws SQLException {
        return DatabasePool.getConnection();
    }

    public static void createTables(Connection connection) throws SQLException {
        DatabaseSchemaManager.createTables(connection);
    }

    public static void addUser(String username, String password, String email, String sex) throws SQLException {
        UserRepository.addUser(username, password, email, sex);
    }

    public static User findUserByUsername(String username) throws SQLException {
        return UserRepository.findUserByUsername(username);
    }

    public static User findUserById(int userId) throws SQLException {
        return UserRepository.findUserById(userId);
    }

    public static void updateUserPassword(int userId, String hashedPassword) throws SQLException {
        UserRepository.updateUserPassword(userId, hashedPassword);
    }

    public static void addQuestion(String question, String optionA, String optionB, String optionC,
                                   String optionD, char answer) throws SQLException {
        QuestionRepository.addQuestion(question, optionA, optionB, optionC, optionD, answer);
    }

    public static List<Questions> listQuestions() throws SQLException {
        return QuestionRepository.listQuestions();
    }

    public static List<Questions> listRandomQuestions(int count) throws SQLException {
        return QuestionRepository.listRandomQuestions(count);
    }

    public static Questions getQuestionById(int questionId) throws SQLException {
        return QuestionRepository.getQuestionById(questionId);
    }

    public static Character getQuestionAnswerById(int questionId) throws SQLException {
        return QuestionRepository.getQuestionAnswerById(questionId);
    }

    public static void updateQuestion(int questionId, String question, String optionA, String optionB,
                                      String optionC, String optionD, char answer) throws SQLException {
        QuestionRepository.updateQuestion(questionId, question, optionA, optionB, optionC, optionD, answer);
    }

    public static void deleteQuestion(int questionId) throws SQLException {
        QuestionRepository.deleteQuestion(questionId);
    }

    public static List<User> listUsers() throws SQLException {
        return UserRepository.listUsers();
    }

    public static void updateUser(int userId, String username, String email, String sex, String password)
            throws SQLException {
        UserRepository.updateUser(userId, username, email, sex, password);
    }

    public static void cancelUser(int userId) throws SQLException {
        UserRepository.cancelUser(userId);
    }
}
