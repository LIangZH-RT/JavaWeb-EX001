package cloud.liang.User;

import cloud.liang.common.SecurityUtil;
import cloud.liang.mySqlSetting.SqlConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");

        String username = trimToEmpty(req.getParameter("username"));
        String password = trimToEmpty(req.getParameter("password"));
        String email = trimToEmpty(req.getParameter("email"));
        String sex = normalizeSex(req.getParameter("sex"));

        if (username.isBlank() || password.isBlank() || email.isBlank() || sex == null) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "Registration information is incomplete.");
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        try {
                User existingUser = SqlConnector.findUserByUsername(username);
                if (existingUser != null) {
                    req.setAttribute("status", "fail");
                    req.setAttribute("message", existingUser.isActive()
                            ? "Username already exists."
                            : "This username has been cancelled. Please use another username.");
                    req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
                    return;
                }

            SqlConnector.addUser(username, password, email, sex);
            SecurityUtil.login(req, username);
            req.setAttribute("status", "success");
            req.setAttribute("message", "Registration successful. Current user: " + username);
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("Registration failed.", e);
        }
    }

    private String normalizeSex(String sex) {
        if (sex == null) {
            return null;
        }

        if ("M".equalsIgnoreCase(sex) || "male".equalsIgnoreCase(sex)) {
            return "男";
        }

        if ("F".equalsIgnoreCase(sex) || "female".equalsIgnoreCase(sex)) {
            return "女";
        }

        return null;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
