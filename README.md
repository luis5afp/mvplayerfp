# MV Play

Repositorio de trabajo para **MV Play**, preparado a partir del APK original.

## Build modificada actual

- Nombre visible: **MV Play**
- Package / applicationId: `com.ftsol.splowtvsm`
- versionName: **`3.1`**
- versionCode: **`111`**
- Modo invitado: **activado**
- Botón: **ENTRAR INVITADO!**
- Destino del invitado: `NewDashboardActivity`

## APK original de referencia

- versionName original: `3.0.9.1`
- versionCode original: `110`
- minSdk: `17`
- targetSdk: `29`
- Código propio detectado principalmente bajo `com.ftsol.akyhay`

## Estado

Ya se analizaron y decodificaron localmente los recursos del APK. Se pudieron convertir a texto legible más de 2,700 XML y extraer 1,510 strings del paquete de recursos.

El script `tools/agregar_invitado.py` genera la build 3.1 con modo Invitado a partir del APK original exacto.

## Descompilar manualmente en Windows

1. Instala Java 17 o superior.
2. Coloca el APK original como `original/MV-Play.apk`.
3. Ejecuta:

```powershell
.\tools\decompilar.ps1
```

## Recompilar

```powershell
.\tools\recompilar.ps1
```

El APK reconstruido debe firmarse con un keystore antes de instalarlo.

## Aviso de privacidad

Este repositorio actualmente es **público**. Una descompilación completa puede exponer URLs, configuraciones y otras cadenas incluidas dentro del APK.
