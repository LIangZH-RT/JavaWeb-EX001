<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="考试成绩" scope="page" />
<c:set var="pageId" value="exam" scope="page" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>考试成绩</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body>
<%@ include file="/jsp/common/layout-start.jspf" %>
<%@ include file="/jsp/common/notice.jspf" %>

<div class="score-board">
    <div class="metric">
        <span>本次得分</span>
        <strong><c:out value="${empty score ? 0 : score}" /></strong>
        <small>分</small>
    </div>
    <div class="metric">
        <span>答对题数</span>
        <strong><c:out value="${empty rightCount ? 0 : rightCount}" /></strong>
        <small>题</small>
    </div>
    <div class="metric">
        <span>总题数</span>
        <strong><c:out value="${empty totalCount ? 0 : totalCount}" /></strong>
        <small>题</small>
    </div>
</div>

<div class="surface">
    <div class="actions">
        <a class="btn" href="${pageContext.request.contextPath}/Exam">重新考试</a>
        <a class="btn secondary" href="${pageContext.request.contextPath}/index.jsp">返回首页入口</a>
    </div>
</div>

<%@ include file="/jsp/common/layout-end.jspf" %>
</body>
</html>
