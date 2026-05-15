package cloud.liang.Question;

import cloud.liang.common.SecurityUtil;
import cloud.liang.mySqlSetting.SqlConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/question")
public class ServletQuestion extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");

        if (!SecurityUtil.isValidCsrfToken(req)) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "表单已失效，请刷新页面后重新提交。");
            forwardQuestionForm(req, resp);
            return;
        }

        String action = req.getParameter("action");
        if ("update".equals(action)) {
            updateQuestion(req, resp);
            return;
        }

        if ("delete".equals(action)) {
            deleteQuestion(req, resp);
            return;
        }

        addQuestion(req, resp);
    }

    private void addQuestion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QuestionFormData formData = readFormData(req);
        String errorMessage = validateQuestionForm(formData, false);
        if (errorMessage != null) {
            showQuestionFormError(req, resp, formData, "add", errorMessage);
            return;
        }

        try {
            SqlConnector.addQuestion(
                    formData.question,
                    formData.answerA,
                    formData.answerB,
                    formData.answerC,
                    formData.answerD,
                    formData.answer.charAt(0)
            );
            req.setAttribute("status", "success");
            req.setAttribute("message", "添加成功！");
            forwardQuestionForm(req, resp);
        } catch (SQLException e) {
            e.printStackTrace();
            showQuestionFormError(req, resp, formData, "add", "添加失败，请稍后再试。");
        }
    }

    private void updateQuestion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        QuestionFormData formData = readFormData(req);
        String errorMessage = validateQuestionForm(formData, true);
        if (errorMessage != null) {
            showQuestionFormError(req, resp, formData, "update", errorMessage);
            return;
        }

        try {
            SqlConnector.updateQuestion(
                    Integer.parseInt(formData.questionId),
                    formData.question,
                    formData.answerA,
                    formData.answerB,
                    formData.answerC,
                    formData.answerD,
                    formData.answer.charAt(0)
            );
            resp.sendRedirect(req.getContextPath() + "/QMatch");
        } catch (SQLException e) {
            e.printStackTrace();
            showQuestionFormError(req, resp, formData, "update", "更新失败，请稍后再试。");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");

        String action = req.getParameter("action");

        if ("edit".equals(action)) {
            editQuestion(req, resp);
            return;
        }

        SecurityUtil.getOrCreateCsrfToken(req);
        req.getRequestDispatcher("/jsp/questionPage.jsp").forward(req, resp);
    }

    private void deleteQuestion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String questionId = req.getParameter("questionId");
        if (!isPositiveInteger(questionId)) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "试题编号无效，无法删除。");
            req.getRequestDispatcher("/QMatch").forward(req, resp);
            return;
        }

        try {
            SqlConnector.deleteQuestion(Integer.parseInt(questionId));
            resp.sendRedirect(req.getContextPath() + "/QMatch");
        } catch (SQLException e) {
            throw new ServletException("删除试题失败", e);
        }
    }

    private void editQuestion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String questionId = req.getParameter("questionId");
        if (!isPositiveInteger(questionId)) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "试题编号无效，无法编辑。");
            req.getRequestDispatcher("/QMatch").forward(req, resp);
            return;
        }

        try {
            Questions question = SqlConnector.getQuestionById(Integer.parseInt(questionId));
            if (question == null) {
                req.setAttribute("status", "fail");
                req.setAttribute("message", "试题不存在，无法编辑。");
                req.getRequestDispatcher("/QMatch").forward(req, resp);
                return;
            }

            req.setAttribute("questionInfo", question);
            req.setAttribute("formAction", "update");
            forwardQuestionForm(req, resp);
        } catch (SQLException e) {
            throw new ServletException("读取试题失败", e);
        }
    }

    private void showQuestionFormError(HttpServletRequest req, HttpServletResponse resp, QuestionFormData formData,
                                       String formAction, String message) throws ServletException, IOException {
        req.setAttribute("status", "fail");
        req.setAttribute("message", message);
        req.setAttribute("questionInfo", formData.toQuestion());
        req.setAttribute("formAction", formAction);
        forwardQuestionForm(req, resp);
    }

    private void forwardQuestionForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SecurityUtil.getOrCreateCsrfToken(req);
        req.getRequestDispatcher("/jsp/questionPage.jsp").forward(req, resp);
    }

    private QuestionFormData readFormData(HttpServletRequest req) {
        QuestionFormData formData = new QuestionFormData();
        formData.questionId = trimToEmpty(req.getParameter("questionId"));
        formData.question = trimToEmpty(req.getParameter("question"));
        formData.answerA = trimToEmpty(req.getParameter("answerA"));
        formData.answerB = trimToEmpty(req.getParameter("answerB"));
        formData.answerC = trimToEmpty(req.getParameter("answerC"));
        formData.answerD = trimToEmpty(req.getParameter("answerD"));
        formData.answer = trimToEmpty(req.getParameter("answer")).toUpperCase();
        return formData;
    }

    private String validateQuestionForm(QuestionFormData formData, boolean requireQuestionId) {
        if (requireQuestionId && !isPositiveInteger(formData.questionId)) {
            return "试题编号无效，无法更新。";
        }

        if (formData.question.isBlank()
                || formData.answerA.isBlank()
                || formData.answerB.isBlank()
                || formData.answerC.isBlank()
                || formData.answerD.isBlank()) {
            return "题目和四个选项都不能为空。";
        }

        if (!formData.answer.matches("[ABCD]")) {
            return "请选择正确答案 A、B、C 或 D。";
        }

        return null;
    }

    private boolean isPositiveInteger(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static class QuestionFormData {
        private String questionId;
        private String question;
        private String answerA;
        private String answerB;
        private String answerC;
        private String answerD;
        private String answer;

        private Questions toQuestion() {
            int id = 0;
            try {
                id = questionId == null || questionId.isBlank() ? 0 : Integer.parseInt(questionId);
            } catch (NumberFormatException ignored) {
                id = 0;
            }

            char answerChar = answer == null || answer.isBlank() ? '\0' : answer.charAt(0);
            return new Questions(id, question, answerA, answerB, answerC, answerD, answerChar);
        }
    }
}
