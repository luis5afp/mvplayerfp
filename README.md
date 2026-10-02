# MV Play

Repositorio de trabajo para **MV Play**, preparado a partir del APK original.

## Importante

Un archivo APK ya está compilado. GitHub puede guardar el APK, pero **no contiene el proyecto Android Studio original**. Para modificarlo de forma práctica hay que descompilarlo.

Este paquete incluye el APK original y scripts para descompilar/recompilar con **Apktool**.

## Descompilar en Windows

1. Instala Java 17 o superior.
2. Abre PowerShell en la carpeta del repositorio.
3. Ejecuta:

```powershell
.\tools\decompilar.ps1
```

Esto creará `decompiled/`, donde podrás editar recursos XML y código Smali.

## Recompilar en Windows

```powershell
.\tools\recompilar.ps1
```

El APK reconstruido quedará en `build/MV-Play-unsigned.apk`.

> El APK reconstruido no conserva automáticamente la firma original. Para instalarlo/distribuirlo tendrás que firmarlo con tu propio keystore.

## APK original

Archivo esperado: `original/MV-Play.apk` (~76 MB). La estructura y herramientas de trabajo ya están preparadas en este repositorio.
