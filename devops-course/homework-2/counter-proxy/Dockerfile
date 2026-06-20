FROM nginx:alpine

RUN apk add --no-cache openssl

RUN mkdir -p /etc/nginx/ssl && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/key.pem \
    -out /etc/nginx/ssl/cert.pem \
    -subj "/C=RU/ST=Saint-Petersburg/L=Saint-Petersburg/O=Company/CN=localhost"

COPY files/nginx.conf /etc/nginx/nginx.conf

EXPOSE 10131

CMD ["nginx", "-g", "daemon off;"]
