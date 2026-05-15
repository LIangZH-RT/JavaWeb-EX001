<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>JSP 页面入口</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body class="entry-page">
<div class="entry-shell">
    <h1>JSP 页面入口</h1>
    <div class="entry-grid">
        <a class="entry-link" href="${pageContext.request.contextPath}/jsp/mainpage.jsp">
            <strong>账号管理</strong>
            <span>/jsp/mainpage.jsp</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/login">
            <strong>用户登录</strong>
            <span>/login</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/register">
            <strong>用户注册</strong>
            <span>/register</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/UserSearch">
            <strong>用户查询</strong>
            <span>/UserSearch</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/question">
            <strong>试题新增</strong>
            <span>/question</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/QMatch">
            <strong>试题查询</strong>
            <span>/QMatch</span>
        </a>
        <a class="entry-link" href="${pageContext.request.contextPath}/Exam">
            <strong>参加考试</strong>
            <span>/Exam</span>
        </a>
    </div>
</div>
</body>
</html>
