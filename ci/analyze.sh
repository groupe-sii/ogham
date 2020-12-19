#!/bin/bash

set -e${DEBUG_COMMANDS:+x}

# Sonar
./mvnw clean install sonar:sonar -Dmaven.test.redirectTestOutputToFile=true -B

# Codecov
bash <(curl -s https://codecov.io/bash)