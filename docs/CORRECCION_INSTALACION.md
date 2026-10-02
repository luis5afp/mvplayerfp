# Corrección de instalación

La APK original usa el package `com.ftsol.splowtvsm` y está firmada con un certificado distinto.

Sin el keystore original no es posible instalar una build modificada como actualización sobre esa app conservando el mismo package.

La build final de desarrollo usa:

- package: `com.ftsol.mvplayapp`
- versionName: `3.1`
- versionCode: `111`
- botón: `ENTRAR INVITADO`

Este package es distinto tanto de la APK original como de las pruebas anteriores, por lo que Android puede instalarla como una app independiente sin el conflicto de firma que produjo el código de error 5.

SHA-256 de la APK final generada:

`86f36ad249058f82c7eeda45917cd36f85e8ad8586edf96be2836f735f213a39`
