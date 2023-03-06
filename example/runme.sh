#!/usr/bin/env bash

set -x

TEXT_PROVIDER_APP="./target/textprovider-0.1.0.jar"

java \
    -jar "${TEXT_PROVIDER_APP}" \
    "$@"
