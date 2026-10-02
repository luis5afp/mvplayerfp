# MV Play

Repositorio de trabajo para **MV Play**.

## Build modificada actual

- Nombre visible: **MV Play**
- Package / applicationId: `com.ftsol.mvplayapp`
- versionName: **`3.1`**
- versionCode: **`111`**
- Modo invitado: **activado**
- Botón: **ENTRAR INVITADO**
- Destino del invitado: `NewDashboardActivity`

## APK original de referencia

- Package original: `com.ftsol.splowtvsm`
- versionName original: `3.0.9.1`
- versionCode original: `110`
- minSdk: `17`
- targetSdk: `29`

## Corrección de instalación

Las builds de prueba anteriores usaron firmas distintas y Android devolvió código de error 5 al intentar instalar una encima de otra. La build final usa el package independiente `com.ftsol.mvplayapp`.

## APK final actual

Archivo: `MV-Play-v3.1-Invitado-Final.apk`

SHA-256:

`86f36ad249058f82c7eeda45917cd36f85e8ad8586edf96be2836f735f213a39`

## Herramientas

El script `tools/agregar_invitado.py` reproduce el parche sobre el APK original exacto.
