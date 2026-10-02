# Corrección de instalación

La versión original usa el package `com.ftsol.splowtvsm` y está firmada con un certificado distinto.

Sin el keystore original no es posible instalar una build modificada como actualización sobre esa app conservando el mismo package.

Para evitar el error de instalación, la build modificada usa:

- package nuevo: `com.ftsol.splowtv31`
- versionName: `3.1`
- versionCode: `111`

Esto permite instalarla como una app independiente, incluso si la versión original sigue instalada.

La APK corregida local generada en esta sesión tiene SHA-256:

`808162843cc74d5bbe3bce1bffaf7934996416e4cd63e2bf73e24ff2eff83485`
