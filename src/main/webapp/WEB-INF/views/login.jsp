<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign in | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp" />

    <main class="main-content">
        <div class="auth-wrapper">
            <div class="auth-card">
                <div class="auth-header">
                    <div style="width: 44px; height: 44px; background: rgba(255,255,255,0.15); border-radius: 10px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 8px;">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"></path>
                            <polyline points="10 17 15 12 10 7"></polyline>
                            <line x1="15" y1="12" x2="3" y2="12"></line>
                        </svg>
                    </div>
                    <h1>Welcome Back</h1>
                    <p>Sign in to continue to your SameerMart account</p>
                </div>

                <div class="auth-body">
                    <c:if test="${param.registered != null}">
                        <div class="alert alert-success">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                <polyline points="22 4 12 14.01 9 11.01"></polyline>
                            </svg>
                            <span>Account created successfully! Please sign in below.</span>
                        </div>
                    </c:if>

                    <c:if test="${param.logout != null}">
                        <div class="alert alert-info">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            <span>You have signed out successfully.</span>
                        </div>
                    </c:if>

                    <form:form method="post" modelAttribute="loginForm">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                        <form:errors path="*" element="div" cssClass="alert alert-danger" />

                        <div class="form-group">
                            <label for="email" class="form-label">Email Address</label>
                            <form:input id="email" path="email" type="email" autocomplete="email" required="true" class="form-control" placeholder="you@example.com" />
                            <form:errors path="email" cssClass="error" />
                        </div>

                        <div class="form-group" style="margin-bottom: 24px;">
                            <label for="password" class="form-label">Password</label>
                            <form:password id="password" path="password" autocomplete="current-password" required="true" class="form-control" placeholder="••••••••" />
                            <form:errors path="password" cssClass="error" />
                        </div>

                        <button type="submit" class="btn btn-accent btn-lg btn-block">
                            Sign In to Account
                        </button>
                    </form:form>
                </div>

                <div class="auth-footer">
                    Don't have an account yet?
                    <a href="${pageContext.request.contextPath}/register" style="font-weight: 600;">Create an account</a>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
