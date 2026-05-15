package cloud.liang.mySqlSetting.repository;

import cloud.liang.Question.Questions;
import cloud.liang.mySqlSetting.datasource.DatabasePool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QuestionRepository {
    private QuestionRepository() {
    }

    public static void addQuestion(String question, String optionA, String optionB, String optionC,
                                   String optionD, char answer) throws SQLException {
        String sql = "INSERT INTO question(title, optionA, optionB, optionC, optionD, answer) values (?,?,?,?,?,?)";
        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, question);
            statement.setString(2, optionA);
            statement.setString(3, optionB);
            statement.setString(4, optionC);
            statement.setString(5, optionD);
            statement.setString(6, String.valueOf(answer));
            statement.executeUpdate();
        }
    }

    public static List<Questions> listQuestions() throws SQLException {
        List<Questions> questions = new ArrayList<>();
        String sql = "SELECT * FROM question";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                questions.add(buildQuestion(resultSet));
            }
        }

        return questions;
    }

    public static List<Questions> listRandomQuestions(int count) throws SQLException {
        List<Questions> questions = new ArrayList<>();
        String sql = "SELECT * FROM question ORDER BY RAND() LIMIT ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, count);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    questions.add(buildQuestion(resultSet));
                }
            }
        }

        return questions;
    }

    public static Questions getQuestionById(int questionId) throws SQLException {
        String sql = "SELECT * FROM question WHERE questionId = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, questionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return buildQuestion(resultSet);
                }
            }
        }

        return null;
    }

    public static Character getQuestionAnswerById(int questionId) throws SQLException {
        String sql = "SELECT answer FROM question WHERE questionId = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, questionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return getFirstChar(resultSet.getString("answer"));
                }
            }
        }

        return null;
    }

    public static void updateQuestion(int questionId, String question, String optionA, String optionB,
                                      String optionC, String optionD, char answer) throws SQLException {
        String sql =
                "UPDATE question SET title = ?, optionA = ?, optionB = ?, optionC = ?, optionD = ?, answer = ? WHERE questionId = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, question);
            statement.setString(2, optionA);
            statement.setString(3, optionB);
            statement.setString(4, optionC);
            statement.setString(5, optionD);
            statement.setString(6, String.valueOf(answer));
            statement.setInt(7, questionId);
            statement.executeUpdate();
        }
    }

    public static void deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM question WHERE questionId = ?";

        try (Connection connection = DatabasePool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, questionId);
            statement.executeUpdate();
        }
    }

    private static Questions buildQuestion(ResultSet resultSet) throws SQLException {
        return new Questions(
                resultSet.getInt("questionId"),
                resultSet.getString("title"),
                resultSet.getString("optionA"),
                resultSet.getString("optionB"),
                resultSet.getString("optionC"),
                resultSet.getString("optionD"),
                getFirstChar(resultSet.getString("answer"))
        );
    }

    private static char getFirstChar(String value) {
        if (value == null || value.isEmpty()) {
            return '\0';
        }

        return value.charAt(0);
    }
}
