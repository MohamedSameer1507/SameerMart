# ==============================================================================
# Multi-stage Dockerfile for SameerMart
# Stage 1: Build application WAR with Maven & Eclipse Temurin JDK 17
# Stage 2: Run application with Apache Tomcat 9 & Eclipse Temurin JRE 17
# ==============================================================================

# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source code and build production WAR
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Stage 2: Runtime ----
FROM tomcat:9.0-jdk17-temurin

LABEL maintainer="Mohamed Sameer <mohamedsameer1507>" \
      description="SameerMart E-Commerce Platform"

# Clean default Tomcat web applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Create persistent storage directories for uploads and local data
RUN mkdir -p /var/sameermart/uploads /var/sameermart/data && \
    chmod -R 777 /var/sameermart

# Copy packaged WAR to ROOT so application is served at container root (/)
COPY --from=builder /app/target/SameerMart.war /usr/local/tomcat/webapps/ROOT.war

# Default environment configuration
ENV SAMEERMART_UPLOAD_DIR=/var/sameermart/uploads \
    CATALINA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

EXPOSE 8080

# Dynamic port binding for Cloud Platforms (Render, Railway, Heroku)
CMD ["sh", "-c", "if [ -n \"$PORT\" ]; then sed -i \"s/port=\\\"8080\\\"/port=\\\"$PORT\\\"/g\" /usr/local/tomcat/conf/server.xml; fi && catalina.sh run"]
