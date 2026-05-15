package cloud.liang.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

public final class SecurityUtil {
    public static final String LOGIN_USER_SESSION_KEY = "loginUserName";
    private static final String CSRF_TOKEN_SESSION_KEY = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private SecurityUtil() {
    }

    public static String getLoginUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object loginUserName = session.getAttribute(LOGIN_USER_SESSION_KEY);
        return loginUserName instanceof String ? (String) loginUserName : null;
    }

    public static void login(HttpServletRequest request, String userName) {
        request.getSession().setAttribute(LOGIN_USER_SESSION_KEY, userName);
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoginUserName(request) != null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        return "admin".equals(getLoginUserName(request));
    }

    public static String getOrCreateCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object existingToken = session.getAttribute(CSRF_TOKEN_SESSION_KEY);
        if (existingToken instanceof String && !((String) existingToken).isBlank()) {
            request.setAttribute("csrfToken", existingToken);
            return (String) existingToken;
        }

        byte[] tokenBytes = new byte[32];
        RANDOM.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        session.setAttribute(CSRF_TOKEN_SESSION_KEY, token);
        request.setAttribute("csrfToken", token);
        return token;
    }

    public static boolean isValidCsrfToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        Object expectedToken = session.getAttribute(CSRF_TOKEN_SESSION_KEY);
        String actualToken = request.getParameter("csrfToken");
        return expectedToken instanceof String
                && actualToken != null
                && expectedToken.equals(actualToken);
    }
}
