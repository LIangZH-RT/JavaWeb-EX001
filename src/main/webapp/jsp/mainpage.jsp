<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="账号管理" scope="page" />
<c:set var="pageId" value="mainpage" scope="page" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>账号管理</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body>
<%@ include file="/jsp/common/layout-start.jspf" %>
<%@ include file="/jsp/common/notice.jspf" %>

<div class="surface">
    <div class="toolbar">
        <h2>账号入口</h2>
        <div class="actions">
            <c:if test="${not empty sessionScope.loginUserName}">
                <a class="btn secondary" href="${pageContext.request.contextPath}/logout">退出登录</a>
            </c:if>
        </div>
    </div>

    <div class="entry-grid account-grid">
        <a class="entry-link" href="${pageContext.request.contextPath}/login">
            <strong>用户登录</strong>
            <span>登录时只需要用户名和密码，不需要邮箱。</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/register">
            <strong>用户注册</strong>
            <span>新用户注册时填写用户名、密码、性别和邮箱。</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/UserSearch">
            <strong>账号查询</strong>
            <span>查看当前系统中的用户列表。</span>
        </a>
    </div>
</div>

<%@ include file="/jsp/common/layout-end.jspf" %>
</body>
</html>
