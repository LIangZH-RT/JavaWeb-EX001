<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="开始考试" scope="page" />
<c:set var="pageId" value="exam" scope="page" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>开始考试</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body>
<%@ include file="/jsp/common/layout-start.jspf" %>
<%@ include file="/jsp/common/notice.jspf" %>

<c:choose>
    <c:when test="${empty questionList}">
        <div class="surface">
            <div class="empty-state">暂无试题，请先新增试题后再开始考试。</div>
        </div>
    </c:when>
    <c:otherwise>
        <form class="surface grid-form" action="${pageContext.request.contextPath}/Exam" method="post">
            <c:forEach var="q" items="${questionList}" varStatus="s">
                <div class="exam-item">
                    <h3><c:out value="${s.count}" />. <c:out value="${q.context}" /></h3>
                    <div class="option-list">
                        <label><input type="radio" name="answer_${q.ID}" value="A" required> A. <c:out value="${q.answerA}" /></label>
                        <label><input type="radio" name="answer_${q.ID}" value="B"> B. <c:out value="${q.answerB}" /></label>
                        <label><input type="radio" name="answer_${q.ID}" value="C"> C. <c:out value="${q.answerC}" /></label>
                        <label><input type="radio" name="answer_${q.ID}" value="D"> D. <c:out value="${q.answerD}" /></label>
                    </div>
                </div>
            </c:forEach>

            <div class="actions">
                <button type="submit" class="btn">提交试卷</button>
                <button type="reset" class="btn secondary">重置</button>
            </div>
        </form>
    </c:otherwise>
</c:choose>

<%@ include file="/jsp/common/layout-end.jspf" %>
</body>
</html>
