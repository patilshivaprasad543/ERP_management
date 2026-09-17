#!/usr/bin/env bash
set -euo pipefail
export SPRING_PROFILES_ACTIVE=eclipse
mvn spring-boot:run
