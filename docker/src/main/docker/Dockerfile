#FROM eclipse-temurin:21-jre-jammy
FROM eclipse-temurin:21

COPY maven/app.jar /app/app.jar
COPY entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

RUN groupadd --gid 2000 appgroup && \
    useradd --create-home --uid 2000 --gid appgroup appuser

RUN chown appuser /app
RUN chmod 700 /app

USER appuser

WORKDIR /app/

EXPOSE 8090

ENV JAVA_MIN_MEM="-Xms64m"
ENV JAVA_MAX_MEM="-Xmx128m"

ENTRYPOINT ["/app/entrypoint.sh"]