<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create account | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp" />

    <main class="main-content">
        <div class="auth-wrapper">
            <div class="auth-card" style="max-width: 500px;">
                <div class="auth-header">
                    <div style="width: 44px; height: 44px; background: rgba(255,255,255,0.15); border-radius: 10px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 8px;">
                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                            <circle cx="8.5" cy="7" r="4"></circle>
                            <line x1="20" y1="8" x2="20" y2="14"></line>
                            <line x1="23" y1="11" x2="17" y2="11"></line>
                        </svg>
                    </div>
                    <h1>Create Account</h1>
                    <p>Join SameerMart to buy or sell quality products</p>
                </div>

                <div class="auth-body">
                    <form:form method="post" modelAttribute="registrationForm">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

                        <form:errors path="*" element="div" cssClass="alert alert-danger" />

                        <div class="form-group">
                            <label for="name" class="form-label">Full Name</label>
                            <form:input id="name" path="name" maxlength="150" autocomplete="name" required="true" class="form-control" placeholder="John Doe" />
                            <form:errors path="name" cssClass="error" />
                        </div>

                        <div class="form-group">
                            <label for="email" class="form-label">Email Address</label>
                            <form:input id="email" path="email" type="email" maxlength="254" autocomplete="email" required="true" class="form-control" placeholder="you@example.com" />
                            <form:errors path="email" cssClass="error" />
                        </div>

                        <div class="form-group">
                            <label for="password" class="form-label">Password (min 8 characters)</label>
                            <form:password id="password" path="password" minlength="8" maxlength="72" autocomplete="new-password" required="true" class="form-control" placeholder="••••••••" />
                            <form:errors path="password" cssClass="error" />
                        </div>

                        <div class="form-group" style="margin-bottom: 24px;">
                            <label for="role" class="form-label">Account Role</label>
                            <form:select id="role" path="role" required="true" class="form-control">
                                <form:option value="" label="Select your role" />
                                <form:option value="BUYER" label="Buyer — Shop and purchase products" />
                                <form:option value="SELLER" label="Seller — List and manage store items" />
                            </form:select>
                            <form:errors path="role" cssClass="error" />
                        </div>

                        <button type="submit" class="btn btn-accent btn-lg btn-block">
                            Create Account
                        </button>
                    </form:form>
                </div>

                <div class="auth-footer">
                    Already registered?
                    <a href="${pageContext.request.contextPath}/login" style="font-weight: 600;">Sign in here</a>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
