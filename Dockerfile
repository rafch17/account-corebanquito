FROM eclipse-temurin:21-jre-alpine
COPY build/libs/account-0.1.jar account-0.1.jar
ENTRYPOINT [ "java", "-jar", "account-0.1.jar" ]
EXPOSE 8080
RUN apk --update --no-cache add curl
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
LABEL version="0.1" \
    description="Account microservice using Postgres\
    including Docker containers and health check test"