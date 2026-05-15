package cloud.liang.Question;


import cloud.liang.mySqlSetting.SqlConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/QMatch")
public class QuestionMatch extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");

        try {
            List<Questions> qs = SqlConnector.listQuestions();
            req.setAttribute("questionList",qs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        req.getRequestDispatcher("/jsp/Q2page.jsp").forward(req,resp);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req ,resp);
    }
}
