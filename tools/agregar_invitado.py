#!/usr/bin/env python3
"""
Parche específico para MV-Play.apk 3.0.9.1.

Cambios:
- Muestra siempre el botón de Free Trial.
- Cambia su texto a "ENTRAR INVITADO!".
- Cambia su acción de FreeTrailActivity a NewDashboardActivity.

IMPORTANTE: verifica el SHA-256 exacto antes de modificar para evitar
aplicar offsets sobre otra versión del APK.
"""
import hashlib
import os
import struct
import sys
import zipfile
import zlib

EXPECTED_APK_SHA256 = "50606985e2bdfd0344b5f0e31b71b4c9c71294dec348346e2278363b1735f099"

def sha256_file(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()

def main():
    if len(sys.argv) != 3:
        raise SystemExit("Uso: python tools/agregar_invitado.py original/MV-Play.apk build/MV-Play-Invitado-unsigned.apk")

    src, out = sys.argv[1], sys.argv[2]
    actual = sha256_file(src)
    if actual != EXPECTED_APK_SHA256:
        raise SystemExit(f"APK no compatible. SHA-256 obtenido: {actual}")

    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)

    with zipfile.ZipFile(src, "r") as zin:
        dex = bytearray(zin.read("classes.dex"))
        arsc = bytearray(zin.read("resources.arsc"))

        # LoginActivity.f0(): elimina la rama que ocultaba btn_free_trail.
        off_if = 0x3CBDE0 + 16 + 2 * 0x27
        expected_branch = bytes.fromhex("38000600")
        if dex[off_if:off_if + 4] != expected_branch:
            raise SystemExit("No se encontró la instrucción esperada de visibilidad.")
        dex[off_if:off_if + 4] = b"\x00\x00\x00\x00"

        # LoginActivity$e.onClick():
        # FreeTrailActivity type@06c2 -> NewDashboardActivity type@08a8.
        off_type = 0x3C9AE0 + 16 + 2 * 7
        if dex[off_type:off_type + 2] != struct.pack("<H", 0x06C2):
            raise SystemExit("No se encontró FreeTrailActivity en el offset esperado.")
        dex[off_type:off_type + 2] = struct.pack("<H", 0x08A8)

        # Recalcula firma SHA-1 y checksum Adler32 del DEX.
        dex[12:32] = hashlib.sha1(dex[32:]).digest()
        dex[8:12] = struct.pack("<I", zlib.adler32(dex[12:]) & 0xFFFFFFFF)

        old = b"Get A Free Trial"
        new = b"ENTRAR INVITADO!"
        if len(old) != len(new) or arsc.count(old) != 1:
            raise SystemExit("No se encontró el texto esperado en resources.arsc.")
        arsc = arsc.replace(old, new, 1)

        with zipfile.ZipFile(out, "w", allowZip64=True) as zout:
            for info in zin.infolist():
                if info.filename.startswith("META-INF/"):
                    continue
                content = zin.read(info.filename)
                if info.filename == "classes.dex":
                    content = bytes(dex)
                elif info.filename == "resources.arsc":
                    content = bytes(arsc)

                zi = zipfile.ZipInfo(info.filename, info.date_time)
                zi.compress_type = info.compress_type
                zi.comment = info.comment
                zi.extra = info.extra
                zi.internal_attr = info.internal_attr
                zi.external_attr = info.external_attr
                zi.create_system = info.create_system
                zi.flag_bits = info.flag_bits
                zout.writestr(zi, content)

    print(f"APK parcheada sin firmar: {out}")
    print(f"SHA-256: {sha256_file(out)}")
    print("Debes firmarla antes de instalarla.")

if __name__ == "__main__":
    main()
