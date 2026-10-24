"""Writes the 7x4x7 stone-floor structure used by the mod's GameTests (data/terrautils/structure/platform.nbt)."""
import gzip
import os
import struct

OUT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "data", "terrautils", "structure", "platform.nbt")
SIZE = (7, 4, 7)
DATA_VERSION = 3955  # 1.21.1


def s(name):
    b = name.encode("utf-8")
    return struct.pack(">H", len(b)) + b


def tag_int(name, v):
    return b"\x03" + s(name) + struct.pack(">i", v)


def tag_string(name, v):
    return b"\x08" + s(name) + s(v)


def int_list(name, values):
    return b"\x09" + s(name) + b"\x03" + struct.pack(">i", len(values)) + b"".join(struct.pack(">i", v) for v in values)


def compound_list(name, compounds):
    return b"\x09" + s(name) + (b"\x0a" if compounds else b"\x00") + struct.pack(">i", len(compounds)) + b"".join(
        c + b"\x00" for c in compounds)


blocks = []
for x in range(SIZE[0]):
    for z in range(SIZE[2]):
        blocks.append(int_list("pos", [x, 0, z]) + tag_int("state", 0))

root = (int_list("size", list(SIZE))
        + compound_list("palette", [tag_string("Name", "minecraft:stone")])
        + compound_list("blocks", blocks)
        + compound_list("entities", [])
        + tag_int("DataVersion", DATA_VERSION))
data = b"\x0a" + s("") + root + b"\x00"

os.makedirs(os.path.dirname(OUT), exist_ok=True)
with gzip.open(OUT, "wb") as f:
    f.write(data)
print("wrote", os.path.abspath(OUT))
