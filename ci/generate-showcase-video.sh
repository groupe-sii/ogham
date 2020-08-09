#!/bin/sh

set -e

SCRIPT=$(readlink -f "$0")
SCRIPT_DIR=$(dirname "$SCRIPT")
SHOWCASE_DIR="$SCRIPT_DIR/../.tools/showcase-recorder"
OUT_DIR="${1:-target/site/presentation}"

docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" up --abort-on-container-exit
docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" down

mkdir -p "$OUT_DIR"
cp -f "/tmp/showcase-clipped.mp4" "$OUT_DIR/showcase.mp4"
cp -f "/tmp/showcase.png" "$OUT_DIR/"

echo "Showcase video and preview generated in '$OUT_DIR'"