# Homework-1

### Ansible роль, которая разворачивает Nginx веб-сервер в Docker контейнере.

Роль предусматривает настройку веб-сервера как по HTTP, так и по HTTPS. В случае HTTPS генерируется самоподписанный сертификат.

Роль поддерживает пользовательские конфигурации Nginx через шаблоны и прокидывание статического контента в веб-сервер из указанной директории.

---

### Локальный запуск

#### 1) Для локального запуска требуется установить коллекции:

```bash
ansible-galaxy collection install community.docker community.crypto
```

#### 2) Запуск с указанием инвентарного файла

```bash
ansible-playbook -i inventory.yaml playbook.yaml
```

---

### Пример запуска

```bash
ansible-playbook -i ./inventories/main/hosts.yaml docker_nginx.play.yaml
```

---

### Ansible переменные

#### Defaults-переменные

```yml
docker_nginx__name: nginx # Название Docker контейнера
docker_nginx__image: nginx:latest # Версия Doker-образа Nginx
docker_nginx__ports: # Порты
  - "80:80"
  - "443:443"
docker_nginx__server_name: default_server # Название Nginx сервера

docker_nginx__nginx_version: 1.18.0-6ubuntu14.7 # Версия Nginx
docker_nginx__config_src: nginx.conf.j2 # Название конфигурации Nginx
docker_nginx__config_dest: /etc/nginx/nginx.conf # Путь до Nginx конфигурации

docker_nginx__ssl_certs_dest: /etc/ssl/certs # Путь до SSL сертификатов 
docker_nginx__ssl_private_dest: /etc/ssl/private # Путь до приватных ключей

docker_nginx__static_src: static/ # Путь до директории со статическим контентом
docker_nginx__static_dest: /usr/share/nginx/html # Путь для статических файлов в контейнере
```

#### Vars-переменные

```yml
docker_nginx__use_https: true # Использование HTTPS
docker_nginx__use_static: true # Использование статического контента

docker_nginx__config_temp: /etc/nginx/nginx.new.conf # Путь до новой Nginx конфигурации для валидации корректности
```
