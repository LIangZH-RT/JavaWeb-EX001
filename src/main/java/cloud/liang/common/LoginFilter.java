package cloud.liang.common;

import cloud.liang.User.User;
import cloud.liang.mySqlSetting.SqlConnector;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebFilter(urlPatterns = {"/Exam", "/UserSearch"})
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        if (isActiveLogin(request)) {
            chain.doFilter(request, response);
            return;
        }

        SecurityUtil.logout(request);
        request.setAttribute("status", "fail");
        request.setAttribute("message", "Please log in before using this function.");
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    private boolean isActiveLogin(HttpServletRequest request) throws ServletException {
        String loginUserName = SecurityUtil.getLoginUserName(request);
        if (loginUserName == null) {
            return false;
        }

        try {
            User user = SqlConnector.findUserByUsername(loginUserName);
            return user != null && user.isActive();
        } catch (SQLException e) {
            throw new ServletException("Failed to verify login user.", e);
        }
    }
}
