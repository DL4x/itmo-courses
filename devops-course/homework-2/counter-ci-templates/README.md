# Counter-ci-templates

Counter-ci-templates — это набор модульных и переиспользуемых шаблонов GitLab CI/CD для автоматизации сборки, тестирования и развертывания  веб-приложения, состоящего из фронтенда и бэкенда.

```yml
.
├── jobs # Job Templates для пайплайнов
│   ├── dotenv-push.yaml
│   ├── git-strategy.yaml
│   ├── kaniko.yaml
│   ├── npm.yaml
│   └── poetry.yaml
├── pipelines # Пайплайны приложения
│   ├── build-backend.yaml # Пайплайн бэкенда
│   ├── build-frontend.yaml # Пайплайн фронтенда
│   └── deploy.yaml # Пайплайн деплоя
├── rules # Общие правила CI
│   └── main.yaml
└── variables # Переменные окружения
    ├── default.yaml
    ├── dotenv.yaml
    ├── harbor.yaml
    ├── main.yaml
    └── vars.yaml
```

#### Для деплоя приложения используется сторонний проект counter-deploy (и docker-compose)

```yml
services:
  frontend:
    image: $counter_frontend_IMAGE
  backend:
    image: $counter_backend_IMAGE
  nginx:
    image: nginx:1.20
    volumes:
      - ./files/default.conf:/etc/nginx/conf.d/default.conf:ro
    ports:
      - 10132:10132/tcp
```
