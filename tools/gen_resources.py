"""Writes the mod's JSON resources (models, recipes, tags, damage types, lang).

Run: python tools/gen_resources.py
"""
import json
import os

BASE = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources")
NS = "terrautils"


def write(rel, obj):
    path = os.path.join(BASE, rel)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        json.dump(obj, f, ensure_ascii=False, indent=2)
        f.write("\n")


ITEMS = ["backpack_upgrade", "hotbar_upgrade", "stack_upgrade", "stomping_horse_armor"]
for item in ITEMS:
    write(f"assets/{NS}/models/item/{item}.json",
          {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{item}"}})


def shaped(name, pattern, key, result, count=1):
    write(f"data/{NS}/recipe/{name}.json", {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "pattern": pattern,
        "key": {k: {"item": v} for k, v in key.items()},
        "result": {"id": result, "count": count},
    })


shaped("backpack_upgrade", ["LDL", "LCL", "LLL"],
       {"L": "minecraft:leather", "D": "minecraft:diamond", "C": "minecraft:chest"}, f"{NS}:backpack_upgrade")
shaped("hotbar_upgrade", ["GRG", "ECE", "GRG"],
       {"G": "minecraft:gold_ingot", "R": "minecraft:redstone", "E": "minecraft:ender_pearl", "C": "minecraft:chest"},
       f"{NS}:hotbar_upgrade")
shaped("stack_upgrade", ["ADA", "DSD", "ADA"],
       {"A": "minecraft:amethyst_shard", "D": "minecraft:diamond", "S": "minecraft:shulker_shell"}, f"{NS}:stack_upgrade")
shaped("stomping_horse_armor", ["OAO", "IHI", "OIO"],
       {"O": "minecraft:obsidian", "A": "minecraft:anvil", "I": "minecraft:iron_ingot", "H": "minecraft:iron_horse_armor"},
       f"{NS}:stomping_horse_armor")

# Dyeable -> HorseArmorLayer tints the (leather) texture with the item's dyed color; players can re-dye it.
write("data/minecraft/tags/item/dyeable.json", {"replace": False, "values": [f"{NS}:stomping_horse_armor"]})

write(f"data/{NS}/damage_type/inferno_aura.json",
      {"message_id": f"{NS}.inferno_aura", "exhaustion": 0.1, "scaling": "when_caused_by_living_non_player", "effects": "burning"})
write(f"data/{NS}/damage_type/horse_stomp.json",
      {"message_id": f"{NS}.horse_stomp", "exhaustion": 0.1, "scaling": "when_caused_by_living_non_player"})
# The aura ticks every second; don't shove mobs around each time.
write("data/minecraft/tags/damage_type/no_knockback.json", {"replace": False, "values": [f"{NS}:inferno_aura"]})


def potion_names(effect_key, en, zh):
    out_en, out_zh = {}, {}
    forms = [("potion", "Potion of {}", "{}药水"), ("splash_potion", "Splash Potion of {}", "喷溅型{}药水"),
             ("lingering_potion", "Lingering Potion of {}", "滞留型{}药水"), ("tipped_arrow", "Arrow of {}", "{}之箭")]
    for form, fen, fzh in forms:
        key = f"item.minecraft.{form}.effect.{NS}.{effect_key}"
        out_en[key] = fen.format(en)
        out_zh[key] = fzh.format(zh)
    return out_en, out_zh


en = {
    "itemGroup.terrautils": "Terra Utilities",
    "item.terrautils.backpack_upgrade": "Expansion Satchel",
    "item.terrautils.backpack_upgrade.desc": "Use: permanently unlocks one more backpack row (+9 slots). Items overflow into it when your inventory is full.",
    "item.terrautils.hotbar_upgrade": "Hotbar Extender",
    "item.terrautils.hotbar_upgrade.desc": "Use: permanently unlocks one more hotbar row. Press the Cycle Hotbar key (R) to swap rows.",
    "item.terrautils.stack_upgrade": "Stack Amplifier",
    "item.terrautils.stack_upgrade.desc": "Use: permanently doubles stack sizes in your inventory per level (max 99 per stack).",
    "item.terrautils.stomping_horse_armor": "Stomping Horse Armor",
    "item.terrautils.stomping_horse_armor.desc": "When your horse lands from a height while ridden, it deals area damage and takes no fall damage.",
    "tooltip.terrautils.max_level": "Max level: %s",
    "message.terrautils.upgrade.backpack": "Backpack expanded! (%s/%s rows) Press B to open it.",
    "message.terrautils.upgrade.hotbar": "Hotbar extended! (%s/%s extra rows) Press R to cycle.",
    "message.terrautils.upgrade.stack": "Stack size increased! (level %s/%s)",
    "message.terrautils.upgrade.maxed": "This upgrade is already at max level.",
    "message.terrautils.no_upgrades": "Use an Expansion Satchel or Hotbar Extender first.",
    "container.terrautils.extended_inventory": "Extended Inventory",
    "container.terrautils.backpack": "Backpack",
    "container.terrautils.hotbar_rows": "Hotbar Rows",
    "container.terrautils.hotbar_hint": "[%s] cycle",
    "button.terrautils.open_backpack": "Extended Inventory (%s)",
    "key.categories.terrautils": "Terra Utilities",
    "key.terrautils.open_backpack": "Open Extended Inventory",
    "key.terrautils.cycle_hotbar": "Cycle Hotbar Rows (Shift = reverse)",
    "effect.terrautils.spelunker": "Spelunker",
    "effect.terrautils.treasure_sense": "Treasure Sense",
    "effect.terrautils.inferno_aura": "Inferno",
    "death.attack.terrautils.inferno_aura": "%1$s was burned to a crisp",
    "death.attack.terrautils.inferno_aura.player": "%1$s was burned to a crisp by %2$s's inferno",
    "death.attack.terrautils.horse_stomp": "%1$s was stomped flat",
    "death.attack.terrautils.horse_stomp.player": "%1$s was stomped flat by %2$s",
}
zh = {
    "itemGroup.terrautils": "泰拉工具集",
    "item.terrautils.backpack_upgrade": "扩容挎包",
    "item.terrautils.backpack_upgrade.desc": "右键使用：永久解锁一行额外背包（+9格）。物品栏满时拾取的物品会自动放入。",
    "item.terrautils.hotbar_upgrade": "快捷栏扩展器",
    "item.terrautils.hotbar_upgrade.desc": "右键使用：永久解锁一行额外快捷栏。按切换键（R）轮换快捷栏。",
    "item.terrautils.stack_upgrade": "堆叠增幅器",
    "item.terrautils.stack_upgrade.desc": "右键使用：每级使你物品栏中的堆叠上限翻倍（单组最多99）。",
    "item.terrautils.stomping_horse_armor": "践踏马铠",
    "item.terrautils.stomping_horse_armor.desc": "骑乘时马从高处落地会对周围造成范围伤害，且马与骑手免疫摔落伤害。",
    "tooltip.terrautils.max_level": "最高等级：%s",
    "message.terrautils.upgrade.backpack": "背包已扩容！（%s/%s 行）按 B 打开。",
    "message.terrautils.upgrade.hotbar": "快捷栏已扩展！（额外 %s/%s 行）按 R 轮换。",
    "message.terrautils.upgrade.stack": "堆叠上限提升！（等级 %s/%s）",
    "message.terrautils.upgrade.maxed": "该升级已达最高等级。",
    "message.terrautils.no_upgrades": "请先使用扩容挎包或快捷栏扩展器。",
    "container.terrautils.extended_inventory": "扩展物品栏",
    "container.terrautils.backpack": "额外背包",
    "container.terrautils.hotbar_rows": "额外快捷栏",
    "container.terrautils.hotbar_hint": "[%s] 轮换",
    "button.terrautils.open_backpack": "扩展物品栏（%s）",
    "key.categories.terrautils": "泰拉工具集",
    "key.terrautils.open_backpack": "打开扩展物品栏",
    "key.terrautils.cycle_hotbar": "轮换快捷栏（Shift 反向）",
    "effect.terrautils.spelunker": "洞穴探险",
    "effect.terrautils.treasure_sense": "寻宝",
    "effect.terrautils.inferno_aura": "狱火",
    "death.attack.terrautils.inferno_aura": "%1$s 被烧成了灰烬",
    "death.attack.terrautils.inferno_aura.player": "%1$s 被 %2$s 的狱火烧成了灰烬",
    "death.attack.terrautils.horse_stomp": "%1$s 被踩扁了",
    "death.attack.terrautils.horse_stomp.player": "%1$s 被 %2$s 的战马踩扁了",
}
for key, name_en, name_zh in [("spelunker", "Spelunker", "洞穴探险"), ("treasure_sense", "Treasure Sense", "寻宝"),
                              ("inferno", "Inferno", "狱火")]:
    e, z = potion_names(key, name_en, name_zh)
    en.update(e)
    zh.update(z)

write(f"assets/{NS}/lang/en_us.json", en)
write(f"assets/{NS}/lang/zh_cn.json", zh)
print("resources written")
