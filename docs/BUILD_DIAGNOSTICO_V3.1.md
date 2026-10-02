# Build de diagnóstico v3.1

APK generada para diagnóstico de tráfico de MV Play.

- versionName: `3.1`
- versionCode: `111`
- package: `com.ftsol.mvplayd01`
- SHA-256: `864e35c9278c7b467394d3a99725c8141cb3e527b1c5da6c95a71a95849e5e35`

## Carpeta creada por la app

`Android/data/com.ftsol.mvplayd01/files/diagnostico/`

## Grupos actuales

- `api`
- `m3u`
- `live`
- `movie`
- `series`

La build intercepta respuestas JSON de los flujos de acceso/configuración principales y las líneas que pasan por los parsers M3U. Los archivos se escriben en modo append.

Esta build no duplica los bytes completos de vídeo/audio de los streams; registra las líneas/URLs y datos textuales que pasan por esos parsers.

## Seguridad

Esta build es para diagnóstico interno. Los datos capturados son crudos y pueden incluir información sensible entregada por el servidor. No debe distribuirse públicamente sin una política de redacción/limpieza.
