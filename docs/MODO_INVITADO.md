# Modo invitado

La build modificada actual de MV Play queda identificada como:

- `versionName = 3.1`
- `versionCode = 111`

Se reutilizó el botón oculto `btn_free_trail` de la pantalla de login.

El parche:

- fuerza el botón a visible;
- cambia el texto de **Get A Free Trial** a **ENTRAR INVITADO!**;
- cambia el destino del click de `FreeTrailActivity` a `NewDashboardActivity`;
- actualiza la versión de la APK a **3.1 / 111**.

## Alcance

El invitado omite la pantalla de autenticación y abre el Dashboard. No crea credenciales de servidor. Las secciones que dependan de `username`, `password` o `serverUrl` pueden quedar vacías hasta que se defina una fuente de contenido para invitados.

## Compatibilidad

El parche está ligado al APK original con SHA-256:

`50606985e2bdfd0344b5f0e31b71b4c9c71294dec348346e2278363b1735f099`

No debe aplicarse a otra versión sin volver a localizar los offsets.
