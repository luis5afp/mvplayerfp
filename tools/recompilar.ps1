$ErrorActionPreference = 'Stop'
$version = '3.0.3'
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$jar = Join-Path $root ".tools\apktool_$version.jar"
$src = Join-Path $root 'decompiled'
$buildDir = Join-Path $root 'build'
$out = Join-Path $buildDir 'MV-Play-unsigned.apk'
if (!(Test-Path $jar)) { throw 'Primero ejecuta tools\decompilar.ps1' }
if (!(Test-Path $src)) { throw 'No existe decompiled/. Ejecuta primero tools\decompilar.ps1' }
New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
java -jar $jar b $src -o $out
Write-Host "APK sin firmar: $out"
