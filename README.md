# Terra Utilities

English | [简体中文](README.zh-CN.md)

A Minecraft mod inspired by Terraria, bringing permanent inventory upgrades, exploration potions, and stomping horse armor to your adventures.

## Requirements

| Component | Version |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.264 or a later compatible version |
| Java | 21 |
| Mod ID | `terrautils` |

Place the built JAR in your game instance's `mods` directory. For multiplayer, install the mod on both the server and all clients.

## Features

### Permanent upgrades

Right-click while holding an upgrade item to consume it and permanently improve the corresponding ability. Upgrade levels persist through death.

| Item | Effect per upgrade | Maximum |
| --- | --- | --- |
| Expansion Satchel | Adds one backpack row with 9 storage slots | 4 rows, 36 slots total |
| Hotbar Extender | Adds one extra hotbar row that you can cycle through | 3 rows, 27 slots total |
| Stack Amplifier | Doubles the stack limit for stackable items | 3 levels, capped at 99 items per stack |

Items that normally cannot stack remain unstackable. Stack upgrades apply to the player's inventory and the mod's extra storage.

When your inventory is full, picked-up items try to go into your extra backpack. Items in extra storage follow the `keepInventory` game rule on death: otherwise, they drop and respect Curse of Vanishing.

The code also registers an extra hotbar slot upgrade, `terrautils:hotbar_slots_upgrade`, which unlocks up to 4 slots mapped to `0`, `-`, `=`, and the backtick key, in that order. This item currently has no crafting recipe, dedicated item model, or localized text.

### Exploration and combat potions

Use an Awkward Potion as the base in a brewing stand:

| Potion | Ingredient | Effect | Default duration |
| --- | --- | --- | --- |
| Spelunker | Raw Gold | Highlights nearby ores within a default radius of 24 blocks | 3 minutes |
| Treasure Sense | Compass | Highlights nearby chests, barrels, shulker boxes, suspicious blocks, decorated pots, spawners, and vaults within a default radius of 48 blocks | 3 minutes |
| Inferno | Fire Charge | Deals continuous area damage to nearby targets; level I defaults to a 4-block radius and 3 damage per second | 3 minutes |

Add Redstone Dust to any of these potions to extend its duration to 8 minutes. Add Glowstone Dust to a regular Inferno Potion to brew Inferno II, lasting 1 minute 30 seconds.

### Stomping Horse Armor

Equip a horse with Stomping Horse Armor to deal area damage and knockback when landing while ridden. The armor also protects the horse and rider from fall damage.

By default, stomps require a fall of at least 3 blocks and deal 2 base damage per block fallen, capped at 40 base damage. Actual damage decreases with distance from the landing point. Stomps do not hurt other players by default.

## Default controls

| Key | Action |
| --- | --- |
| `B` | Open the extended inventory |
| `R` | Cycle hotbar rows |
| `Shift` + `R` | Cycle hotbar rows in reverse |

You can also open extra storage using the extension button in the inventory screen. The `B` and `R` bindings can be changed in the game's controls settings.

## Crafting recipes

All patterns below use a crafting table and produce one item. Rows are listed from top to bottom.

| Item | Pattern | Ingredients |
| --- | --- | --- |
| Expansion Satchel | `LDL` / `LCL` / `LLL` | `L` Leather, `D` Diamond, `C` Chest |
| Hotbar Extender | `GRG` / `ECE` / `GRG` | `G` Gold Ingot, `R` Redstone Dust, `E` Ender Pearl, `C` Chest |
| Stack Amplifier | `ADA` / `DSD` / `ADA` | `A` Amethyst Shard, `D` Diamond, `S` Shulker Shell |
| Stomping Horse Armor | `OAO` / `IHI` / `OIO` | `O` Obsidian, `A` Anvil, `I` Iron Ingot, `H` Iron Horse Armor |

## Configuration

After the first launch, the common configuration is available at `config/terrautils-common.toml` in your game instance.

| Setting | Default | Description |
| --- | --- | --- |
| `potions.spelunkerRadius` | `24` | Spelunker scan radius |
| `potions.treasureRadius` | `48` | Treasure Sense scan radius |
| `potions.infernoDamage` | `3.0` | Inferno I damage per second |
| `potions.infernoRadius` | `4.0` | Inferno I aura radius |
| `stomping_horse_armor.minFallDistance` | `3.0` | Minimum fall distance required to trigger a stomp |
| `stomping_horse_armor.damagePerBlock` | `2.0` | Stomp damage per block fallen |
| `stomping_horse_armor.maxDamage` | `40.0` | Maximum base stomp damage |
| `stomping_horse_armor.hurtsPlayers` | `false` | Whether stomps can hurt other players |

## Development and building

Install JDK 21, then use the included Gradle Wrapper from the project root. The first build requires an internet connection to download Gradle and dependencies.

On Windows PowerShell:

```powershell
# Build the mod
.\gradlew.bat build

# Launch the development client
.\gradlew.bat runClient

# Launch the development server
.\gradlew.bat runServer

# Run GameTests
.\gradlew.bat runGameTestServer
```

On Linux and macOS, use `./gradlew` with the same tasks.

Build outputs are placed in `build/libs/`; the current version produces `terrautils-1.0.0.jar`. See [gradle.properties](gradle.properties) for the mod version, display name, and loader version settings.

## License

The mod metadata declares the MIT license.
