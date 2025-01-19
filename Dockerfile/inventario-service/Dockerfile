FROM openjdk:17-jdk-slim

# Actualizar mirrors (opcional, dependiendo de la región)
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/' /etc/apt/sources.list \
    && sed -i 's/security.debian.org/mirrors.aliyun.com/' /etc/apt/sources.list

# Instalar dependencias necesarias
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
    fontconfig \
    libfreetype6 \
    ; \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY ROOT.jar app.jar
EXPOSE 8098
ENTRYPOINT ["java", "-jar", "app.jar"]
