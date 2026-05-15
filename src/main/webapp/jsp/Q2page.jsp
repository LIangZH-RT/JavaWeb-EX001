<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ page import="cloud.liang.common.SecurityUtil" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    SecurityUtil.getOrCreateCsrfToken(request);
%>
<c:set var="pageTitle" value="试题查询" scope="page" />
<c:set var="pageId" value="question-list" scope="page" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>试题查询</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body>
<%@ include file="/jsp/common/layout-start.jspf" %>
<%@ include file="/jsp/common/notice.jspf" %>

<div class="surface">
    <div class="toolbar">
        <h2>试题列表</h2>
        <div class="actions">
            <a class="btn" href="${pageContext.request.contextPath}/question">新增试题</a>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty questionList}">
            <div class="empty-state">暂无试题，请先新增试题。</div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table">
                    <thead>
                    <tr>
                        <th>编号</th>
                        <th>题目</th>
                        <th>A</th>
                        <th>B</th>
                        <th>C</th>
                        <th>D</th>
                        <th>答案</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="q" items="${questionList}">
                        <tr>
                            <td><c:out value="${q.ID}" /></td>
                            <td><c:out value="${q.context}" /></td>
                            <td><c:out value="${q.answerA}" /></td>
                            <td><c:out value="${q.answerB}" /></td>
                            <td><c:out value="${q.answerC}" /></td>
                            <td><c:out value="${q.answerD}" /></td>
                            <td><c:out value="${q.answer}" /></td>
                            <td>
                                <div class="table-actions">
                                    <a class="link-action" href="${pageContext.request.contextPath}/question?action=edit&questionId=${q.ID}">更新</a>
                                    <form class="inline-form" action="${pageContext.request.contextPath}/question" method="post"
                                          onsubmit="return confirm('确定删除该试题吗？')">
                                        <input type="hidden" name="csrfToken" value="${fn:escapeXml(csrfToken)}">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="questionId" value="${fn:escapeXml(q.ID)}">
                                        <button type="submit" class="link-danger">删除</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/jsp/common/layout-end.jspf" %>
</body>
</html>
