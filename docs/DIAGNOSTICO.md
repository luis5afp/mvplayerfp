# Diagnóstico de tráfico MV Play

La carpeta se crea en tiempo de ejecución en:

`Android/data/com.ftsol.mvplayapp/files/diagnostico/`

No se escribe dentro del APK porque el APK instalado es de solo lectura.

## Organización

```text
diagnostico/
├── index.jsonl
└── YYYY-MM-DD/
    └── SESION/
        ├── api_app/
        ├── xtream_player_api/
        ├── epg_xmltv/
        ├── live/
        ├── vod/
        ├── series/
        ├── whmcs/
        ├── vpn/
        ├── streams/
        └── other/
```

Cada request/response puede producir:

- metadatos en JSON;
- body en `.json`, `.xml`, `.m3u` o `.txt`;
- errores separados;
- índice global `index.jsonl`.

## Seguridad

Por defecto se redactan:

- password / passwd;
- tokens;
- Authorization;
- cookies;
- api_key / apikey.

## Límites

- Máximo 5 MiB por body.
- Retención aproximada: 14 días.
- Límite global objetivo: 300 MiB.
- Para streams de vídeo/audio se guardan metadatos y errores, no los bytes completos del vídeo.

## Grupos detectados en esta APK

La APK utiliza varias rutas/librerías de red, incluyendo Xtream `player_api.php`, `xmltv.php`, llamadas de la API propia, WHMCS, VPN y tráfico de streams. La integración debe enganchar los puntos de red después de descompilar el APK base real.

## Estado del APK base en GitHub

Actualmente `original/MV-Play.apk` en el repositorio pesa 1 byte. Debe reemplazarse por el APK original completo (~76 MiB) antes de ejecutar la integración automática.
