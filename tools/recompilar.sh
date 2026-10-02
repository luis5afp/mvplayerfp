#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JAR="$ROOT/.tools/apktool_3.0.3.jar"
mkdir -p "$ROOT/build"
java -jar "$JAR" b "$ROOT/decompiled" -o "$ROOT/build/MV-Play-unsigned.apk"
echo "APK sin firmar: $ROOT/build/MV-Play-unsigned.apk"
