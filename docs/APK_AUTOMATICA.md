# APK automática

Este repositorio está preparado para mantener una APK actualizada en:

`releases/latest/MV-Play.apk`

y una copia histórica por versión:

`releases/v<VERSION>/MV-Play-v<VERSION>.apk`

## Cómo funciona

Cuando cambia cualquiera de estos archivos:

- `original/MV-Play.apk`
- `tools/agregar_invitado.py`
- `VERSION`

GitHub Actions ejecuta el parche, genera la APK y la firma con una clave de **desarrollo** guardada en la caché privada de Actions.

Después el workflow hace commit de la APK generada al propio repositorio.

## Primera carga

El archivo base debe existir una vez en:

`original/MV-Play.apk`

El APK original pesa aproximadamente 76 MiB. GitHub permite ese tamaño mediante Git, aunque no mediante el cargador web normal de archivos grandes.

## Firma

La firma automática configurada aquí es solo para desarrollo/pruebas. Mientras GitHub conserve la caché de la clave, las builds automáticas conservarán la misma firma y podrán actualizarse unas sobre otras.

Para distribución estable/producción se debe usar un keystore privado propio mediante GitHub Actions Secrets.
