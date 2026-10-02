# Modo invitado

Se identificó que la pantalla de login ya incluía un botón oculto `btn_free_trail`.

En la versión original:

1. Una condición de configuración decide si el botón se muestra.
2. Al pulsarlo, `LoginActivity$e.onClick()` abre `FreeTrailActivity`.

El parche de invitado realiza tres cambios:

- fuerza el botón a visible;
- cambia el texto de **Get A Free Trial** a **ENTRAR INVITADO!**;
- cambia el destino del click a `NewDashboardActivity`.

## Alcance

Este modo omite la pantalla de autenticación y abre el Dashboard. No inventa credenciales ni concede acceso servidor a contenido que la API proteja. Si una sección necesita `username`, `password` o `serverUrl`, esa sección puede quedar vacía o mostrar un error hasta que se defina qué contenido debe ver un invitado.

## Compatibilidad

El parche incluido en `tools/agregar_invitado.py` está ligado exactamente al APK con SHA-256:

`50606985e2bdfd0344b5f0e31b71b4c9c71294dec348346e2278363b1735f099`

No debe aplicarse a otra versión sin volver a localizar los offsets.
