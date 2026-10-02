# MV Play

Repositorio de trabajo para **MV Play**, preparado a partir del APK original.

## Datos confirmados del APK

- Package / applicationId: `com.ftsol.splowtvsm`
- Nombre visible: **MV Play**
- versionName: `3.0.9.1`
- versionCode: `110`
- minSdk: `17`
- targetSdk: `29`
- Código propio detectado principalmente bajo `com.ftsol.akyhay`

## Estado

Ya se analizaron y decodificaron localmente los recursos del APK. Se pudieron convertir a texto legible más de 2,700 XML y extraer 1,510 strings del paquete de recursos.

Para tener el proyecto **completamente editable y reconstruible** en GitHub falta que exista el binario:

```
original/MV-Play.apk
```

Cuando ese archivo esté en el repo, el workflow **Decompile APK** ejecutará Apktool y creará automáticamente `decompiled/`, incluyendo recursos y Smali.

## Descompilar manualmente en Windows

1. Instala Java 17 o superior.
2. Coloca el APK como `original/MV-Play.apk`.
3. Ejecuta:

```powershell
.\tools\decompilar.ps1
```

## Recompilar

```powershell
.\tools\recompilar.ps1
```

El resultado se crea como `build/MV-Play-unsigned.apk`. Después hay que firmarlo con un keystore propio.

## Aviso de privacidad

Este repositorio actualmente es **público**. Una descompilación completa puede exponer URLs, configuraciones y otras cadenas incluidas dentro del APK. Si el proyecto debe mantenerse privado, cambia la visibilidad del repositorio antes de subir el APK.
