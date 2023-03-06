#!/usr/bin/env bash

echo "Building ..."

export MAVEN_OPTS="
  --add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
  --add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
  --add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
  --add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
  --add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED "

mvn clean package > build.log 2>&1

if [[ "$?" -eq 0 ]]; then
  echo "Build was successful"
else
  echo "Build failed"
fi

echo "Build output can be found in build.log"
