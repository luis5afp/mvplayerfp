#!/usr/bin/env python3
"""
Parche específico para MV-Play.apk original 3.0.9.1.

Genera una build modificada:
- versionName 3.1
- versionCode 111
- muestra siempre el botón de invitado
- texto "ENTRAR INVITADO!"
- abre NewDashboardActivity sin pasar por FreeTrailActivity

El APK resultante queda SIN FIRMAR.
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

def patch_manifest_version(manifest):
    manifest = bytearray(manifest)
    u16 = lambda o: struct.unpack_from("<H", manifest, o)[0]
    u32 = lambda o: struct.unpack_from("<I", manifest, o)[0]

    sp = 8
    if u16(sp) != 0x0001:
        raise SystemExit("AndroidManifest.xml no tiene el StringPool esperado.")

    header_size = u16(sp + 2)
    chunk_size = u32(sp + 4)
    string_count = u32(sp + 8)
    flags = u32(sp + 16)
    if flags & 0x100:
        raise SystemExit("StringPool UTF-8 inesperado.")

    strings_start = u32(sp + 20)
    offsets_base = sp + header_size
    strings_base = sp + strings_start

    strings = []
    meta = []
    for i in range(string_count):
        rel = u32(offsets_base + 4 * i)
        p = strings_base + rel
        n = u16(p)
        if n & 0x8000:
            raise SystemExit("Longitud UTF-16 extendida no esperada.")
        text_start = p + 2
        s = bytes(manifest[text_start:text_start + 2 * n]).decode("utf-16le")
        strings.append(s)
        meta.append((p, text_start, n))

    matches = [i for i, s in enumerate(strings) if s == "3.0.9.1"]
    if len(matches) != 1:
        raise SystemExit("No se encontró versionName 3.0.9.1 exactamente una vez.")

    idx = matches[0]
    p, text_start, _ = meta[idx]
    new = "3.1"
    struct.pack_into("<H", manifest, p, len(new))
    encoded = new.encode("utf-16le")
    manifest[text_start:text_start + len(encoded)] = encoded
    manifest[text_start + len(encoded):text_start + len(encoded) + 2] = b"\x00\x00"

    pos = sp + chunk_size
    patched_code = False
    while pos < len(manifest):
        typ = u16(pos)
        size = u32(pos + 4)
        if typ == 0x0102:
            ext = pos + 16
            name_idx = u32(ext + 4)
            if strings[name_idx] == "manifest":
                attr_start = u16(ext + 8)
                attr_size = u16(ext + 10)
                attr_count = u16(ext + 12)
                abase = ext + attr_start
                for j in range(attr_count):
                    a = abase + j * attr_size
                    if strings[u32(a + 4)] == "versionCode":
                        if u32(a + 16) != 110:
                            raise SystemExit("versionCode original inesperado.")
                        struct.pack_into("<I", manifest, a + 16, 111)
                        patched_code = True
                        break
                break
        if size <= 0:
            break
        pos += size

    if not patched_code:
        raise SystemExit("No se pudo cambiar versionCode.")
    return bytes(manifest)

def main():
    if len(sys.argv) != 3:
        raise SystemExit("Uso: python tools/agregar_invitado.py original/MV-Play.apk build/MV-Play-v3.1-Invitado-unsigned.apk")

    src, out = sys.argv[1], sys.argv[2]
    actual = sha256_file(src)
    if actual != EXPECTED_APK_SHA256:
        raise SystemExit(f"APK no compatible. SHA-256 obtenido: {actual}")

    os.makedirs(os.path.dirname(out) or ".", exist_ok=True)

    with zipfile.ZipFile(src, "r") as zin:
        dex = bytearray(zin.read("classes.dex"))
        arsc = bytearray(zin.read("resources.arsc"))
        manifest = patch_manifest_version(zin.read("AndroidManifest.xml"))

        off_if = 0x3CBDE0 + 16 + 2 * 0x27
        if dex[off_if:off_if + 4] != bytes.fromhex("38000600"):
            raise SystemExit("No se encontró la instrucción esperada de visibilidad.")
        dex[off_if:off_if + 4] = b"\x00\x00\x00\x00"

        off_type = 0x3C9AE0 + 16 + 2 * 7
        if dex[off_type:off_type + 2] != struct.pack("<H", 0x06C2):
            raise SystemExit("No se encontró FreeTrailActivity en el offset esperado.")
        dex[off_type:off_type + 2] = struct.pack("<H", 0x08A8)

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
                elif info.filename == "AndroidManifest.xml":
                    content = manifest

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
    print("versionName: 3.1")
    print("versionCode: 111")
    print(f"SHA-256: {sha256_file(out)}")
    print("Debes firmarla antes de instalarla.")

if __name__ == "__main__":
    main()
