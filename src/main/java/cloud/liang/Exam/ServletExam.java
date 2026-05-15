package cloud.liang.Exam;

import cloud.liang.Question.Questions;
import cloud.liang.mySqlSetting.SqlConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Exam")
public class ServletExam extends HttpServlet {
    private static final int EXAM_QUESTION_COUNT = 4;
    private static final String EXAM_QUESTION_IDS_SESSION_KEY = "examQuestionIds";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");

        try {
            List<Questions> qs = SqlConnector.listRandomQuestions(EXAM_QUESTION_COUNT);
            req.setAttribute("questionList", qs);
            req.getSession().setAttribute(EXAM_QUESTION_IDS_SESSION_KEY, extractQuestionIds(qs));
        } catch (SQLException e) {
            throw new ServletException("读取考试题目失败", e);
        }

        req.getRequestDispatcher("/jsp/examPage.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");

        HttpSession session = req.getSession(false);
        List<Integer> questionIds = getSessionQuestionIds(session);
        int totalCount = questionIds.size();
        int rightCount = 0;

        try {
            for (Integer id : questionIds) {
                Character rightAnswer = SqlConnector.getQuestionAnswerById(id);
                String userAnswer = req.getParameter("answer_" + id);

                if (rightAnswer != null && userAnswer != null && !userAnswer.isBlank()
                        && Character.toUpperCase(userAnswer.charAt(0)) == Character.toUpperCase(rightAnswer)) {
                    rightCount++;
                }
            }
        } catch (SQLException e) {
            throw new ServletException("考试判分失败", e);
        }

        if (session != null) {
            session.removeAttribute(EXAM_QUESTION_IDS_SESSION_KEY);
        }

        int score = totalCount == 0 ? 0 : rightCount * 100 / totalCount;
        req.setAttribute("score", score);
        req.setAttribute("rightCount", rightCount);
        req.setAttribute("totalCount", totalCount);

        req.getRequestDispatcher("/jsp/scor.jsp").forward(req, resp);
    }

    private List<Integer> extractQuestionIds(List<Questions> questions) {
        List<Integer> questionIds = new ArrayList<>();
        for (Questions question : questions) {
            questionIds.add(question.getID());
        }
        return questionIds;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> getSessionQuestionIds(HttpSession session) {
        if (session == null) {
            return List.of();
        }

        Object value = session.getAttribute(EXAM_QUESTION_IDS_SESSION_KEY);
        if (value instanceof List<?>) {
            return (List<Integer>) value;
        }

        return List.of();
    }
}
