<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="用户登录" scope="page" />
<c:set var="pageId" value="login" scope="page" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户登录</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body>
<%@ include file="/jsp/common/layout-start.jspf" %>
<%@ include file="/jsp/common/notice.jspf" %>

<form class="surface grid-form" action="${pageContext.request.contextPath}/login" method="post">
    <div class="field">
        <label for="username">用户名</label>
        <input type="text" id="username" name="username" required>
    </div>

    <div class="field">
        <label for="password">密码</label>
        <input type="password" id="password" name="password" required>
    </div>

    <div class="actions">
        <button type="submit" class="btn">登录</button>
        <a class="btn secondary" href="${pageContext.request.contextPath}/register">去注册</a>
    </div>
</form>

<%@ include file="/jsp/common/layout-end.jspf" %>
</body>
</html>
