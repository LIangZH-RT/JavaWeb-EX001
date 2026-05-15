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
import java.util.List;


@WebServlet("/UserSearch")
public class UserSearch extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");

        if (!SecurityUtil.isValidCsrfToken(req)) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "表单已失效，请刷新页面后重新提交。");
            forwardUserPage(req, resp);
            return;
        }

        String action = req.getParameter("action");
        if ("update".equals(action)) {
            updateCurrentUser(req, resp);
            return;
        }

        if ("cancel".equals(action)) {
            cancelUser(req, resp);
            return;
        }

        forwardUserPage(req, resp);

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");

        if ("edit".equals(req.getParameter("action"))) {
            req.setAttribute("showEdit", true);
        }

        forwardUserPage(req, resp);
    }

    private void updateCurrentUser(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User currentUser = getCurrentActiveUser(req);
        if (currentUser == null) {
            forwardLoginError(req, resp, "请先登录后再修改个人信息。");
            return;
        }

        UserFormData formData = readUserForm(req);
        String errorMessage = validateUserForm(formData);
        if (errorMessage != null) {
            showUserFormError(req, resp, currentUser, formData, errorMessage);
            return;
        }

        if (!SecurityUtil.isAdmin(req) && "admin".equalsIgnoreCase(formData.username)) {
            showUserFormError(req, resp, currentUser, formData, "普通用户不能修改为管理员账号名。");
            return;
        }

        try {
            User sameNameUser = SqlConnector.findUserByUsername(formData.username);
            if (sameNameUser != null && sameNameUser.getID() != currentUser.getID()) {
                showUserFormError(req, resp, currentUser, formData, "用户名已存在，请更换后再提交。");
                return;
            }

            SqlConnector.updateUser(
                    currentUser.getID(),
                    formData.username,
                    formData.email,
                    formData.sex,
                    formData.password
            );
            SecurityUtil.login(req, formData.username);
            req.setAttribute("status", "success");
            req.setAttribute("message", "个人信息更新成功。");
            forwardUserPage(req, resp);
        } catch (SQLException e) {
            throw new ServletException("更新用户信息失败", e);
        }
    }

    private void cancelUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentActiveUser(req);
        if (currentUser == null) {
            forwardLoginError(req, resp, "请先登录后再注销账号。");
            return;
        }

        String userId = trimToEmpty(req.getParameter("userId"));
        if (!isPositiveInteger(userId)) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "用户编号无效，无法注销。");
            forwardUserPage(req, resp);
            return;
        }

        int targetUserId = Integer.parseInt(userId);
        boolean adminOperation = SecurityUtil.isAdmin(req) && targetUserId != currentUser.getID();
        if (!adminOperation && targetUserId != currentUser.getID()) {
            req.setAttribute("status", "fail");
            req.setAttribute("message", "只能注销本人账号。");
            forwardUserPage(req, resp);
            return;
        }

        try {
            User targetUser = SqlConnector.findUserById(targetUserId);
            if (targetUser == null) {
                req.setAttribute("status", "fail");
                req.setAttribute("message", "用户不存在，无法注销。");
                forwardUserPage(req, resp);
                return;
            }

            SqlConnector.cancelUser(targetUserId);
            if (targetUserId == currentUser.getID()) {
                SecurityUtil.logout(req);
                req.setAttribute("status", "success");
                req.setAttribute("message", "账号已注销，请重新注册或联系管理员。");
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("status", "success");
            req.setAttribute("message", "用户 " + targetUser.getName() + " 已注销。");
            forwardUserPage(req, resp);
        } catch (SQLException e) {
            throw new ServletException("注销用户失败", e);
        }
    }

    private void forwardUserPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentActiveUser(req);
        if (currentUser == null) {
            forwardLoginError(req, resp, "请先登录后再查看用户信息。");
            return;
        }

        try {
            boolean admin = SecurityUtil.isAdmin(req);
            req.setAttribute("isAdmin", admin);
            req.setAttribute("userInfo", currentUser);
            if (admin) {
                List<User> users = SqlConnector.listUsers();
                req.setAttribute("Users", users);
            }
            SecurityUtil.getOrCreateCsrfToken(req);
            req.getRequestDispatcher("/jsp/UserSearch.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException("读取用户信息失败", e);
        }
    }

    private void showUserFormError(HttpServletRequest req, HttpServletResponse resp, User currentUser,
                                   UserFormData formData, String message) throws ServletException, IOException {
        req.setAttribute("status", "fail");
        req.setAttribute("message", message);
        req.setAttribute("showEdit", true);
        req.setAttribute("isAdmin", SecurityUtil.isAdmin(req));
        if (SecurityUtil.isAdmin(req)) {
            try {
                req.setAttribute("Users", SqlConnector.listUsers());
            } catch (SQLException e) {
                throw new ServletException("读取用户列表失败", e);
            }
        }
        req.setAttribute("userInfo", new User(
                currentUser.getID(),
                formData.username,
                null,
                formData.email,
                formData.sex == null ? currentUser.getSex() : formData.sex.charAt(0),
                currentUser.getStatus()
        ));
        SecurityUtil.getOrCreateCsrfToken(req);
        req.getRequestDispatcher("/jsp/UserSearch.jsp").forward(req, resp);
    }

    private User getCurrentActiveUser(HttpServletRequest req) throws ServletException {
        String loginUserName = SecurityUtil.getLoginUserName(req);
        if (loginUserName == null) {
            return null;
        }

        try {
            User user = SqlConnector.findUserByUsername(loginUserName);
            if (user == null || !user.isActive()) {
                return null;
            }

            return user;
        } catch (SQLException e) {
            throw new ServletException("读取当前用户失败", e);
        }
    }

    private void forwardLoginError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        SecurityUtil.logout(req);
        req.setAttribute("status", "fail");
        req.setAttribute("message", message);
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    private UserFormData readUserForm(HttpServletRequest req) {
        UserFormData formData = new UserFormData();
        formData.username = trimToEmpty(req.getParameter("username"));
        formData.password = trimToEmpty(req.getParameter("password"));
        formData.email = trimToEmpty(req.getParameter("email"));
        formData.sex = normalizeSex(req.getParameter("sex"));
        return formData;
    }

    private String validateUserForm(UserFormData formData) {
        if (formData.username.isBlank() || formData.email.isBlank() || formData.sex == null) {
            return "用户名、性别和邮箱不能为空。";
        }

        if (formData.username.length() > 50) {
            return "用户名不能超过 50 个字符。";
        }

        if (formData.email.length() > 50) {
            return "邮箱不能超过 50 个字符。";
        }

        return null;
    }

    private String normalizeSex(String sex) {
        if (sex == null) {
            return null;
        }

        if ("男".equals(sex) || "M".equalsIgnoreCase(sex) || "male".equalsIgnoreCase(sex)) {
            return "男";
        }

        if ("女".equals(sex) || "F".equalsIgnoreCase(sex) || "female".equalsIgnoreCase(sex)) {
            return "女";
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

    private static class UserFormData {
        private String username;
        private String password;
        private String email;
        private String sex;
    }
}
