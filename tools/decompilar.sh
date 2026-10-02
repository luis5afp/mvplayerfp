#!/usr/bin/env bash
set -euo pipefail
VERSION=3.0.3
EXPECTED=dbf930b076c6b9be08d57c449cacefc3bdd6b71ebd59b3066fc0e1f5b14f9423
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
mkdir -p "$ROOT/.tools"
JAR="$ROOT/.tools/apktool_${VERSION}.jar"
if [ ! -f "$JAR" ]; then
  curl -L "https://github.com/iBotPeaches/Apktool/releases/download/v${VERSION}/apktool_${VERSION}.jar" -o "$JAR"
fi
echo "$EXPECTED  $JAR" | sha256sum -c -
rm -rf "$ROOT/decompiled"
java -jar "$JAR" d -f "$ROOT/original/MV-Play.apk" -o "$ROOT/decompiled"
echo "Listo: $ROOT/decompiled"
