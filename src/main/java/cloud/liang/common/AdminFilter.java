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

@WebFilter(urlPatterns = {"/question", "/QMatch", "/jsp/questionPage.jsp"})
public class AdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        AdminCheckResult checkResult = checkAdmin(request);
        if (checkResult.activeAdmin) {
            chain.doFilter(request, response);
            return;
        }

        if (checkResult.shouldLogout) {
            SecurityUtil.logout(request);
        }
        request.setAttribute("status", "fail");
        request.setAttribute("message", "Only admin can access question management.");
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    private AdminCheckResult checkAdmin(HttpServletRequest request) throws ServletException {
        String loginUserName = SecurityUtil.getLoginUserName(request);
        if (loginUserName == null) {
            return new AdminCheckResult(false, false);
        }

        try {
            User user = SqlConnector.findUserByUsername(loginUserName);
            if (user == null || !user.isActive()) {
                return new AdminCheckResult(false, true);
            }

            return new AdminCheckResult("admin".equals(loginUserName), false);
        } catch (SQLException e) {
            throw new ServletException("Failed to verify admin user.", e);
        }
    }

    private static class AdminCheckResult {
        private final boolean activeAdmin;
        private final boolean shouldLogout;

        private AdminCheckResult(boolean activeAdmin, boolean shouldLogout) {
            this.activeAdmin = activeAdmin;
            this.shouldLogout = shouldLogout;
        }
    }
}
