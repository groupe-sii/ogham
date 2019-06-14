#!/bin/bash

# Sonar
./mvnw clean install sonar:sonar -Dmaven.test.redirectTestOutputToFile=true -B | grep -Ei '(warn|error|ANALYSIS SUCCESSFUL)'

# Codecov
bash <(curl -s https://codecov.io/bash)