# MV Play

Repositorio de trabajo para **MV Play**, preparado a partir del APK original.

## Estado del repositorio

Ya están incluidos los scripts y la información necesarios para trabajar con el APK mediante **Apktool**. El binario `original/MV-Play.apk` debe estar presente para ejecutar la descompilación.

## Descompilar en Windows

1. Instala Java 17 o superior.
2. Coloca el APK como `original/MV-Play.apk`.
3. Abre PowerShell en la carpeta del repositorio.
4. Ejecuta:

```powershell
.\tools\decompilar.ps1
```

Esto creará `decompiled/`, donde podrás editar recursos XML y código Smali.

## Recompilar en Windows

```powershell
.\tools\recompilar.ps1
```

El APK reconstruido quedará en `build/MV-Play-unsigned.apk`.

> El APK reconstruido no conserva automáticamente la firma original. Para instalarlo o distribuirlo tendrás que firmarlo con tu propio keystore.

## APK original identificado

- Archivo: `MV-Play.apk`
- Tamaño: 79,298,159 bytes (~76 MiB)
- SHA-256: `50606985e2bdfd0344b5f0e31b71b4c9c71294dec348346e2278363b1735f099`
- Namespace/clases detectadas: `com.ftsol.akyhay`
