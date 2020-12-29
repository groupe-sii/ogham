#!/bin/sh

set -e${DEBUG_COMMANDS:+x}

SCRIPT=$(readlink -f "$0")
SCRIPT_DIR=$(dirname "$SCRIPT")
SHOWCASE_DIR="$SCRIPT_DIR/../.tools/showcase-recorder"
OUT_DIR="${1:-$SCRIPT_DIR/../target/site/presentation}"
UID=$(id -u)
GID=$(id -g)

mkdir -p "$OUT_DIR"

echo "::group::Pull images"
OUTPUT_UID="$UID" OUTPUT_GID="$GID" OUTPUT_DIR="$OUT_DIR" docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" pull
echo "::endgroup::"

echo "::group::Build images"
OUTPUT_UID="$UID" OUTPUT_GID="$GID" OUTPUT_DIR="$OUT_DIR" docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" build --force-rm --pull
echo "::endgroup::"

echo "::group::Start images"
OUTPUT_UID="$UID" OUTPUT_GID="$GID" OUTPUT_DIR="$OUT_DIR" docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" up &

EXITED=""
STARTED=""
# wait for end
while [ -z "$EXITED" ]
do
	sleep 5
	STATUS=$(OUTPUT_UID="$UID" OUTPUT_GID="$GID" OUTPUT_DIR="$OUT_DIR" docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" ps | grep showcase-launcher)
	STARTED=$(echo "$STATUS" | grep -o "Up" || echo "")
	if [ ! -z "$STARTED" ]; then continue; fi

	EXITED=$(echo "$STATUS" | grep -Eo "Exit ([0-9]+)" || echo "")
done
echo "::endgroup::"

echo "::group::Stop containers"
OUTPUT_UID="$UID" OUTPUT_GID="$GID" OUTPUT_DIR="$OUT_DIR" docker-compose -f "$SHOWCASE_DIR/docker-compose.yml" down
echo "::endgroup::"

echo ""

if [ "$EXITED" = "Exit 1" ]; then 
	echo "Failed to generate showcase video"
	exit 1;
fi

echo "Showcase video and preview generated in '$OUT_DIR'"