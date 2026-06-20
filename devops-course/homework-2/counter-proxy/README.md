# Counter-proxy

Конфигурация прокси реализована через Docker и GitLab CI, которые автоматически разворачивают nginx с правилами перенаправления трафика на ВМ №2 и настраивают самоподписанные сертификаты для защищенного HTTPS-соединения.

#### Для проксирования запросов развернута nginx конфигурация
```nginx
events {
    worker_connections 1024;
}

http {
    upstream existing_nginx {
        server 10.101.1.32:10132;
    }

    server {
        listen 10131 ssl;
        server_name _;

        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        
        location / {
            proxy_pass http://existing_nginx;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
}
```

#### Counter доступен публично по FQDN учебной ВМ
```bash
$ curl -k https://group-1-st-16-1.itmo.devops-teta.ru:10131/api/counter

$ {"count": 2}
```
