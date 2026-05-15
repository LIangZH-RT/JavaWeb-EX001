<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<c:set var="pageTitle" value="用户信息" scope="page" />
<c:set var="pageId" value="users" scope="page" />
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>用户信息</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/css/app.css">
</head>
<body>
<%@ include file="/jsp/common/layout-start.jspf" %>
<%@ include file="/jsp/common/notice.jspf" %>

<div class="surface">
    <div class="toolbar">
        <h2>我的信息</h2>
        <div class="actions">
            <c:choose>
                <c:when test="${showEdit}">
                    <a class="btn secondary" href="${pageContext.request.contextPath}/UserSearch">取消编辑</a>
                </c:when>
                <c:otherwise>
                    <a class="btn" href="${pageContext.request.contextPath}/UserSearch?action=edit">修改信息</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <c:choose>
        <c:when test="${showEdit}">
            <form class="grid-form" action="${pageContext.request.contextPath}/UserSearch" method="post">
                <input type="hidden" name="csrfToken" value="${fn:escapeXml(csrfToken)}">
                <input type="hidden" name="action" value="update">

                <div class="field">
                    <label for="username">用户名</label>
                    <input type="text" id="username" name="username" maxlength="50"
                           value="${fn:escapeXml(userInfo.name)}" required>
                </div>

                <div class="field">
                    <label for="password">新密码</label>
                    <input type="password" id="password" name="password" placeholder="不修改请留空">
                </div>

                <div class="field">
                    <label>性别</label>
                    <div class="inline-options">
                        <c:choose>
                            <c:when test="${userInfo.sexText eq '女'}">
                                <label><input type="radio" name="sex" value="M"> 男</label>
                                <label><input type="radio" name="sex" value="F" checked> 女</label>
                            </c:when>
                            <c:otherwise>
                                <label><input type="radio" name="sex" value="M" checked> 男</label>
                                <label><input type="radio" name="sex" value="F"> 女</label>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="field">
                    <label for="email">邮箱</label>
                    <input type="email" id="email" name="email" maxlength="50"
                           value="${fn:escapeXml(userInfo.email)}" required>
                </div>

                <div class="actions">
                    <button type="submit" class="btn">保存修改</button>
                    <a class="btn secondary" href="${pageContext.request.contextPath}/UserSearch">返回</a>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table class="data-table compact-table">
                    <tbody>
                    <tr>
                        <th>ID</th>
                        <td><c:out value="${userInfo.id}" /></td>
                    </tr>
                    <tr>
                        <th>用户名</th>
                        <td><c:out value="${userInfo.name}" /></td>
                    </tr>
                    <tr>
                        <th>性别</th>
                        <td><c:out value="${userInfo.sex}" /></td>
                    </tr>
                    <tr>
                        <th>邮箱</th>
                        <td><c:out value="${userInfo.email}" /></td>
                    </tr>
                    <tr>
                        <th>状态</th>
                        <td><c:out value="${userInfo.statusText}" /></td>
                    </tr>
                    <tr>
                        <th>操作</th>
                        <td>
                            <div class="table-actions">
                                <a class="link-action" href="${pageContext.request.contextPath}/UserSearch?action=edit">更新本人信息</a>
                                <form class="inline-form" action="${pageContext.request.contextPath}/UserSearch" method="post"
                                      onsubmit="return confirm('确定注销当前账号吗？注销后将无法登录。')">
                                    <input type="hidden" name="csrfToken" value="${fn:escapeXml(csrfToken)}">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="userId" value="${fn:escapeXml(userInfo.id)}">
                                    <button type="submit" class="link-danger">注销账号</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<c:if test="${isAdmin}">
    <div class="surface">
        <div class="toolbar">
            <h2>全部用户</h2>
            <div class="actions">
                <a class="btn" href="${pageContext.request.contextPath}/register">新增用户</a>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty Users}">
                <div class="empty-state">暂无用户数据。</div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>性别</th>
                            <th>邮箱</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="u" items="${Users}">
                            <tr>
                                <td><c:out value="${u.id}" /></td>
                                <td><c:out value="${u.name}" /></td>
                                <td><c:out value="${u.sex}" /></td>
                                <td><c:out value="${u.email}" /></td>
                                <td><c:out value="${u.statusText}" /></td>
                                <td>
                                    <div class="table-actions">
                                        <c:choose>
                                            <c:when test="${u.id eq userInfo.id}">
                                                <a class="link-action" href="${pageContext.request.contextPath}/UserSearch?action=edit">更新本人</a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="muted-text">仅本人可更新</span>
                                            </c:otherwise>
                                        </c:choose>

                                        <c:choose>
                                            <c:when test="${u.active}">
                                                <form class="inline-form" action="${pageContext.request.contextPath}/UserSearch" method="post"
                                                      onsubmit="return confirm('确定注销该用户吗？')">
                                                    <input type="hidden" name="csrfToken" value="${fn:escapeXml(csrfToken)}">
                                                    <input type="hidden" name="action" value="cancel">
                                                    <input type="hidden" name="userId" value="${fn:escapeXml(u.id)}">
                                                    <button type="submit" class="link-danger">注销</button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="muted-text">已注销</span>
                                            </c:otherwise>
                                        </c:choose>
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
</c:if>

<%@ include file="/jsp/common/layout-end.jspf" %>
</body>
</html>
