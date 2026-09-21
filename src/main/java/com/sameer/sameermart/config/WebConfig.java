package com.sameer.sameermart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SessionAuthenticationInterceptor sessionAuthenticationInterceptor;

    public WebConfig(SessionAuthenticationInterceptor sessionAuthenticationInterceptor) {
        this.sessionAuthenticationInterceptor = sessionAuthenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionAuthenticationInterceptor)
                .addPathPatterns("/dashboard", "/dashboard/**", "/cart", "/cart/**", "/checkout", "/checkout/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/products");
        registry.addRedirectViewController("/h2-console", "/h2-console/");
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.web.servlet.ServletRegistrationBean<jakarta.servlet.Servlet> h2ConsoleServletRegistration() {
        try {
            Class<?> servletClass = Class.forName("org.h2.server.web.JakartaWebServlet");
            jakarta.servlet.Servlet servlet = (jakarta.servlet.Servlet) servletClass.getDeclaredConstructor().newInstance();
            org.springframework.boot.web.servlet.ServletRegistrationBean<jakarta.servlet.Servlet> registration =
                    new org.springframework.boot.web.servlet.ServletRegistrationBean<>(servlet, "/h2-console/*");
            registration.addInitParameter("webAllowOthers", "false");
            return registration;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize H2 console servlet", e);
        }
    }
}
