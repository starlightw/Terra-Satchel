"""Generates the mod's simple pixel-art textures (items + mob effect icons).

Run: python tools/gen_textures.py
Requires only the standard library (writes PNGs with zlib).
"""
import os
import struct
import zlib

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "terrautils", "textures")


def write_png(path, pixels):
    h = len(pixels)
    w = len(pixels[0])
    raw = b"".join(b"\x00" + b"".join(struct.pack("BBBB", *p) for p in row) for row in pixels)

    def chunk(tag, data):
        c = tag + data
        return struct.pack(">I", len(data)) + c + struct.pack(">I", zlib.crc32(c) & 0xFFFFFFFF)

    png = b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", struct.pack(">IIBBBBB", w, h, 8, 6, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw, 9)) + chunk(b"IEND", b"")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "wb") as f:
        f.write(png)


def hexc(s):
    s = s.lstrip("#")
    return (int(s[0:2], 16), int(s[2:4], 16), int(s[4:6], 16), 255)


def from_art(art, palette, size=None):
    rows = [r for r in art.strip("\n").split("\n")]
    h = len(rows)
    w = max(len(r) for r in rows)
    size = size or (w, h)
    out = [[(0, 0, 0, 0)] * size[0] for _ in range(size[1])]
    ox = (size[0] - w) // 2
    oy = (size[1] - h) // 2
    for y, r in enumerate(rows):
        for x, ch in enumerate(r):
            if ch in palette:
                out[y + oy][x + ox] = palette[ch]
    return out


# ---- Items (16x16) ----

BACKPACK = """
.....oooooo.....
....oBBBBBBo....
....oB....Bo....
..oooooooooooo..
.oBBBBBBBBBBBBo.
.oBbbbbbbbbbbBo.
.oBbbYYYYYbbbBo.
.oBbbYyyyYbbbBo.
.oBbbYYYYYbbbBo.
.oBbbbbbbbbbbBo.
.oBbbbbbbbbbbBo.
.oBBBBBBBBBBBBo.
.oBbbbbbbbbbbBo.
.oBBBBBBBBBBBBo.
..oooooooooooo..
................
"""
BACKPACK_PAL = {"o": hexc("3b2412"), "B": hexc("8a5a2b"), "b": hexc("a8743c"), "Y": hexc("e8c547"), "y": hexc("fff2a0")}

HOTBAR = """
................
oooooooooooooooo
oGgGoGgGoGgGoGgo
oggg.ggg.ggg.ggo
oGgGoGgGoGgGoGgo
oooooooooooooooo
.......aa.......
......aAAa......
.....aAAAAa.....
.......AA.......
oooooooooooooooo
oSsSoSsSoSsSoSso
osss.sss.sss.sso
oSsSoSsSoSsSoSso
oooooooooooooooo
................
"""
HOTBAR_PAL = {"o": hexc("1e1e2a"), "G": hexc("5ec8ff"), "g": hexc("2a7fc0"), "S": hexc("9a9aa8"), "s": hexc("5d5d6b"),
              "a": hexc("1a6b2a"), "A": hexc("52e06a")}

STACK = """
................
.......cc.......
......cCCc......
.....cCwwCc.....
......cCCc......
....cc.cc.cc....
...cCCc..cCCc...
..cCwwCc.cCwwCc.
...cCCc..cCCc...
....cc....cc....
..cc.cc.cc.cc...
.cCCcCCccCCcCCc.
cCwwCCwwCwwCCwwc
.cCCcCCccCCcCCc.
..cc.cc.cc.cc...
................
"""
STACK_PAL = {"c": hexc("4a1d6b"), "C": hexc("a55be0"), "w": hexc("f1d8ff")}

HORSE_ARMOR = """
................
..........kkk...
.........kRRRk..
........kRrrrRk.
.......kRrrrrRk.
......kRrrrrRk..
.....kRrrrrRk...
...kkRrrrrRk....
..kRRrrrrRk.....
.kRrrrrrRk......
kRrrffrrk.......
kRrffffrk.......
.kRffffRk.......
..kRRRRk........
...kkkk.........
................
"""
HORSE_PAL = {"k": hexc("2a1010"), "R": hexc("c0392b"), "r": hexc("7b241c"), "f": hexc("ff9b2e")}

# ---- Mob effect icons (18x18) ----

SPELUNKER = """
.....kkkkkkkk.....
...kkGGGGGGGGkk...
..kGGGGggggGGGGk..
.kGGg........gGGk.
.kGg..dd.......Gk.
kGg..dDDd..yy..gGk
kG...dDDd.yYYy..Gk
kG....dd..yYYy..Gk
kG.........yy...Gk
kG..rr..........Gk
kG.rRRr...ee....Gk
kGg.rr...eEEe..gGk
.kGg......ee...Gk.
.kGGg........gGGk.
..kGGGGggggGGGGk..
...kkGGGGGGGGkk...
.....kkkkkkkk.....
..................
"""
SPELUNKER_PAL = {"k": hexc("2b2b2b"), "G": hexc("d9b23a"), "g": hexc("8a6d1c"), "d": hexc("1aa5a5"), "D": hexc("7af5f0"),
                 "y": hexc("b08a12"), "Y": hexc("ffe14a"), "r": hexc("8a1010"), "R": hexc("ff3b3b"), "e": hexc("127a2e"),
                 "E": hexc("4cff7a")}

TREASURE = """
..................
..................
....kkkkkkkkkk....
...kWWWWWWWWWWk...
..kWwwwwwwwwwwWk..
..kWwwwwwwwwwwWk..
..kkkkkkYYkkkkkk..
..kWwwwwYyYwwwWk..
..kWwwwwYYYwwwWk..
..kWwwwwwwwwwwWk..
..kWwwwwwwwwwwWk..
..kWWWWWWWWWWWWk..
..kkkkkkkkkkkkkk..
.s..............s.
s.s............s.s
.s..............s.
..................
..................
"""
TREASURE_PAL = {"k": hexc("3b2412"), "W": hexc("8a5a2b"), "w": hexc("b07a3e"), "Y": hexc("ffd23a"), "y": hexc("fff6b0"),
                "s": hexc("fff6b0")}

INFERNO = """
..................
........r.........
.......rr....r....
...r...rOr..rr....
...rr.rrOrr.rOr...
..rOr.rOYOr.rOr...
..rOrrOYYOrrOOr...
..rOOOYYYYOOOOr...
.rrOYYYwwYYYOOrr..
.rOOYYwwwwYYYOOr..
.rOYYwwwwwwYYYOr..
.rOYYwwwwwwwYYOr..
.rOYYYwwwwwYYYOr..
..rOYYYYYYYYYOr...
..rrOOOYYYYOOrr...
...rrrOOOOOrrr....
.....rrrrrrr......
..................
"""
INFERNO_PAL = {"r": hexc("8a1a00"), "O": hexc("ff5a00"), "Y": hexc("ffb000"), "w": hexc("fff3a0")}


def main():
    write_png(os.path.join(ROOT, "item", "backpack_upgrade.png"), from_art(BACKPACK, BACKPACK_PAL, (16, 16)))
    write_png(os.path.join(ROOT, "item", "hotbar_upgrade.png"), from_art(HOTBAR, HOTBAR_PAL, (16, 16)))
    write_png(os.path.join(ROOT, "item", "stack_upgrade.png"), from_art(STACK, STACK_PAL, (16, 16)))
    write_png(os.path.join(ROOT, "item", "stomping_horse_armor.png"), from_art(HORSE_ARMOR, HORSE_PAL, (16, 16)))
    write_png(os.path.join(ROOT, "mob_effect", "spelunker.png"), from_art(SPELUNKER, SPELUNKER_PAL, (18, 18)))
    write_png(os.path.join(ROOT, "mob_effect", "treasure_sense.png"), from_art(TREASURE, TREASURE_PAL, (18, 18)))
    write_png(os.path.join(ROOT, "mob_effect", "inferno_aura.png"), from_art(INFERNO, INFERNO_PAL, (18, 18)))
    print("textures written to", os.path.abspath(ROOT))


if __name__ == "__main__":
    main()
