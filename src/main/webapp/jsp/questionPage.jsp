<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ page import="cloud.liang.common.SecurityUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    SecurityUtil.getOrCreateCsrfToken(request);
%>
<c:set var="pageTitle" value="试题新增 / 编辑" scope="page" />
<c:set var="pageId" value="question-form" scope="page" />
<c:set var="currentAnswer" value="${empty questionInfo ? '' : questionInfo.answerText}" scope="page" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>试题新增 / 编辑</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body>
<%@ include file="/jsp/common/layout-start.jspf" %>
<%@ include file="/jsp/common/notice.jspf" %>

<form class="surface grid-form" action="${pageContext.request.contextPath}/question" method="post">
    <input type="hidden" name="csrfToken" value="${fn:escapeXml(csrfToken)}">
    <input type="hidden" name="action" value="${fn:escapeXml(empty formAction ? 'add' : formAction)}">
    <input type="hidden" name="questionId" value="${fn:escapeXml(questionInfo.ID)}">

    <div class="field">
        <label for="question">题目</label>
        <textarea name="question" id="question" required><c:out value="${questionInfo.context}" /></textarea>
    </div>

    <div class="option-grid">
        <div class="field">
            <label for="answerA">A</label>
            <input type="text" id="answerA" name="answerA" value="${fn:escapeXml(questionInfo.answerA)}" required>
        </div>
        <div class="field">
            <label for="answerB">B</label>
            <input type="text" id="answerB" name="answerB" value="${fn:escapeXml(questionInfo.answerB)}" required>
        </div>
        <div class="field">
            <label for="answerC">C</label>
            <input type="text" id="answerC" name="answerC" value="${fn:escapeXml(questionInfo.answerC)}" required>
        </div>
        <div class="field">
            <label for="answerD">D</label>
            <input type="text" id="answerD" name="answerD" value="${fn:escapeXml(questionInfo.answerD)}" required>
        </div>
    </div>

    <div class="field">
        <label>正确答案</label>
        <div class="radio-list">
            <c:choose>
                <c:when test="${currentAnswer eq 'B'}">
                    <label><input type="radio" name="answer" value="A" required> A</label>
                    <label><input type="radio" name="answer" value="B" checked> B</label>
                    <label><input type="radio" name="answer" value="C"> C</label>
                    <label><input type="radio" name="answer" value="D"> D</label>
                </c:when>
                <c:when test="${currentAnswer eq 'C'}">
                    <label><input type="radio" name="answer" value="A" required> A</label>
                    <label><input type="radio" name="answer" value="B"> B</label>
                    <label><input type="radio" name="answer" value="C" checked> C</label>
                    <label><input type="radio" name="answer" value="D"> D</label>
                </c:when>
                <c:when test="${currentAnswer eq 'D'}">
                    <label><input type="radio" name="answer" value="A" required> A</label>
                    <label><input type="radio" name="answer" value="B"> B</label>
                    <label><input type="radio" name="answer" value="C"> C</label>
                    <label><input type="radio" name="answer" value="D" checked> D</label>
                </c:when>
                <c:otherwise>
                    <label><input type="radio" name="answer" value="A" required> A</label>
                    <label><input type="radio" name="answer" value="B"> B</label>
                    <label><input type="radio" name="answer" value="C"> C</label>
                    <label><input type="radio" name="answer" value="D"> D</label>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="actions">
        <c:choose>
            <c:when test="${empty formAction || formAction == 'add'}">
                <button type="submit" class="btn">新增试题</button>
            </c:when>
            <c:otherwise>
                <button type="submit" class="btn">更新试题</button>
                <a class="btn secondary" href="${pageContext.request.contextPath}/QMatch">取消编辑</a>
            </c:otherwise>
        </c:choose>
        <button type="reset" class="btn secondary">重置</button>
    </div>
</form>

<%@ include file="/jsp/common/layout-end.jspf" %>
</body>
</html>
