#!/usr/bin/env bash

set -euo pipefail

PROJECT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_FILE="$PROJECT_DIR/.env.local"

if [[ ! -f "$ENV_FILE" ]]; then
    echo "Error: falta el archivo .env.local"
    exit 1
fi

set -a
source "$ENV_FILE"
set +a

if [[ -z "${BEAGI_DB_PASSWORD:-}" ]]; then
    echo "Error: BEAGI_DB_PASSWORD no está definida"
    exit 1
fi

cd "$PROJECT_DIR"
exec ./mvnw spring-boot:run