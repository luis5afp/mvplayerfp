# Información confirmada del APK

- Archivo analizado: `MV-Play.apk`
- Tamaño: 79,298,159 bytes (~76 MiB)
- SHA-256: `50606985e2bdfd0344b5f0e31b71b4c9c71294dec348346e2278363b1735f099`
- Package: `com.ftsol.splowtvsm`
- Nombre visible: `MV Play`
- versionName: `3.0.9.1`
- versionCode: `110`
- minSdkVersion: `17`
- targetSdkVersion: `29`
- Namespace/clases propias detectadas: `com.ftsol.akyhay`

## Componentes observados

Entre las clases detectadas aparecen `LoginActivity`, `SplashActivity`, `HoneyPlayer`, `SettingsActivity`, `NewEPGActivity`, `SearchActivity`, `TVArchiveActivity`, adaptadores para VOD/series/live y componentes VPN.

Durante el análisis de cadenas DEX se observó, entre otras, la URL `https://techiproz.com/mvplaysm/api/`. Cualquier cambio de endpoint debe hacerse después de localizar todas sus referencias en Smali o JADX.
