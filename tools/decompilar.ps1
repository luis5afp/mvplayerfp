$ErrorActionPreference = 'Stop'
$version = '3.0.3'
$expected = 'dbf930b076c6b9be08d57c449cacefc3bdd6b71ebd59b3066fc0e1f5b14f9423'
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$toolDir = Join-Path $root '.tools'
$jar = Join-Path $toolDir "apktool_$version.jar"
$apk = Join-Path $root 'original\MV-Play.apk'
$out = Join-Path $root 'decompiled'
New-Item -ItemType Directory -Force -Path $toolDir | Out-Null
if (!(Test-Path $jar)) {
  $url = "https://github.com/iBotPeaches/Apktool/releases/download/v$version/apktool_$version.jar"
  Write-Host "Descargando Apktool $version..."
  Invoke-WebRequest -Uri $url -OutFile $jar
}
$hash = (Get-FileHash $jar -Algorithm SHA256).Hash.ToLower()
if ($hash -ne $expected) { throw "SHA-256 de Apktool no coincide. Obtenido: $hash" }
if (Test-Path $out) { Remove-Item $out -Recurse -Force }
java -jar $jar d -f $apk -o $out
Write-Host "Listo: $out"
