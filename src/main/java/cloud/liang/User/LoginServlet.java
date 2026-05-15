package cloud.liang.User;

import cloud.liang.common.PasswordUtil;
import cloud.liang.common.SecurityUtil;
import cloud.liang.mySqlSetting.SqlConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");

        String username = trimToEmpty(req.getParameter("username"));
        String password = trimToEmpty(req.getParameter("password"));

        if (username.isBlank() || password.isBlank()) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "Username and password are required.");
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
            return;
        }

        try {
            User existingUser = SqlConnector.findUserByUsername(username);
            if (existingUser == null) {
                req.setAttribute("status", "fail");
                req.setAttribute("message", "User does not exist.");
            } else if (!existingUser.isActive()) {
                req.setAttribute("status", "fail");
                req.setAttribute("message", "This account has been cancelled and cannot log in.");
            } else if (PasswordUtil.verifyPassword(password, existingUser.getPassword())) {
                SecurityUtil.login(req, username);
                req.setAttribute("status", "success");
                req.setAttribute("message", "Login successful. Current user: " + username);
            } else if (!PasswordUtil.isHashedPassword(existingUser.getPassword())
                    && password.equals(existingUser.getPassword())) {
                SqlConnector.updateUserPassword(existingUser.getID(), PasswordUtil.hashPassword(password));
                SecurityUtil.login(req, username);
                req.setAttribute("status", "success");
                req.setAttribute("message", "Login successful. Legacy password has been upgraded.");
            } else {
                req.setAttribute("status", "fail");
                req.setAttribute("message", "Incorrect password.");
            }
        } catch (SQLException e) {
            throw new ServletException("Login failed.", e);
        }

        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
