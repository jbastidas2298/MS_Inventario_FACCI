# Usa Nginx como servidor web
FROM nginx:alpine

# Copia los certificados al directorio dentro del contenedor
COPY certificados/certificado.crt /etc/nginx/certs/certificado.crt
COPY certificados/certificado.key /etc/nginx/certs/certificado.key

# Copia los archivos de Angular al directorio raíz de Nginx
COPY gestion-inventario /usr/share/nginx/html

# Configura Nginx (asegúrate de que nginx.conf tiene las configuraciones de HTTPS)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expone los puertos HTTP (80) y HTTPS (443)
EXPOSE 80
EXPOSE 443

# Comando para iniciar Nginx
CMD ["nginx", "-g", "daemon off;"]
