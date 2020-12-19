#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

SCRIPT=$(readlink -f "$0")
SCRIPT_DIR=$(dirname "$SCRIPT")
SHOWCASE_DIR="$SCRIPT_DIR/../.tools/showcase-recorder"
OUT_DIR="${1:-$SCRIPT_DIR/../target/site/presentation}"
UID=$(id -u)
GID=$(id -g)

mkdir -p "$OUT_DIR"

OUTPUT_UID="$UID" OUTPUT_GID="$GID" OUTPUT_DIR="$OUT_DIR" docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" up --abort-on-container-exit
OUTPUT_UID="$UID" OUTPUT_GID="$GID" OUTPUT_DIR="$OUT_DIR" docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" down

echo ""
echo "Showcase video and preview generated in '$OUT_DIR'"