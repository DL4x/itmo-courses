ARG VERSION="3.11"


FROM python:${VERSION}-slim

WORKDIR /app

COPY pyproject.toml poetry.lock README.md ./

RUN pip install poetry && \
    poetry config virtualenvs.create false && \
    poetry install --only main --no-root

COPY src/ ./src/

ENV PYTHONPATH=/app/src:$PYTHONPATH

EXPOSE 8080

CMD ["python", "-m", "counter_backend"]

