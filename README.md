# Terra Utilities · 泰拉工具集

[English](#english) | [简体中文](#简体中文)

## English

A Minecraft mod inspired by Terraria, bringing permanent inventory upgrades, exploration potions, and stomping horse armor to your adventures.

### Requirements

| Component | Version |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.264 or a later compatible version |
| Java | 21 |
| Mod ID | `terrautils` |

Place the built JAR in your game instance's `mods` directory. For multiplayer, install the mod on both the server and all clients.

### Features

#### Permanent upgrades

Right-click while holding an upgrade item to consume it and permanently improve the corresponding ability. Upgrade levels persist through death.

| Item | Effect per upgrade | Maximum |
| --- | --- | --- |
| Expansion Satchel | Adds one backpack row with 9 storage slots | 4 rows, 36 slots total |
| Hotbar Extender | Adds one extra hotbar row that you can cycle through | 3 rows, 27 slots total |
| Stack Amplifier | Doubles the stack limit for stackable items | 3 levels, capped at 99 items per stack |

Items that normally cannot stack remain unstackable. Stack upgrades apply to the player's inventory and the mod's extra storage.

When your inventory is full, picked-up items try to go into your extra backpack. Items in extra storage follow the `keepInventory` game rule on death: otherwise, they drop and respect Curse of Vanishing.

The code also registers an extra hotbar slot upgrade, `terrautils:hotbar_slots_upgrade`, which unlocks up to 4 slots mapped to `0`, `-`, `=`, and the backtick key, in that order. This item currently has no crafting recipe, dedicated item model, or localized text.

#### Exploration and combat potions

Use an Awkward Potion as the base in a brewing stand:

| Potion | Ingredient | Effect | Default duration |
| --- | --- | --- | --- |
| Spelunker | Raw Gold | Highlights nearby ores within a default radius of 24 blocks | 3 minutes |
| Treasure Sense | Compass | Highlights nearby chests, barrels, shulker boxes, suspicious blocks, decorated pots, spawners, and vaults within a default radius of 48 blocks | 3 minutes |
| Inferno | Fire Charge | Deals continuous area damage to nearby targets; level I defaults to a 4-block radius and 3 damage per second | 3 minutes |

Add Redstone Dust to any of these potions to extend its duration to 8 minutes. Add Glowstone Dust to a regular Inferno Potion to brew Inferno II, lasting 1 minute 30 seconds.

#### Stomping Horse Armor

Equip a horse with Stomping Horse Armor to deal area damage and knockback when landing while ridden. The armor also protects the horse and rider from fall damage.

By default, stomps require a fall of at least 3 blocks and deal 2 base damage per block fallen, capped at 40 base damage. Actual damage decreases with distance from the landing point. Stomps do not hurt other players by default.

### Default controls

| Key | Action |
| --- | --- |
| `B` | Open the extended inventory |
| `R` | Cycle hotbar rows |
| `Shift` + `R` | Cycle hotbar rows in reverse |

You can also open extra storage using the extension button in the inventory screen. The `B` and `R` bindings can be changed in the game's controls settings.

### Crafting recipes

All patterns below use a crafting table and produce one item. Rows are listed from top to bottom.

| Item | Pattern | Ingredients |
| --- | --- | --- |
| Expansion Satchel | `LDL` / `LCL` / `LLL` | `L` Leather, `D` Diamond, `C` Chest |
| Hotbar Extender | `GRG` / `ECE` / `GRG` | `G` Gold Ingot, `R` Redstone Dust, `E` Ender Pearl, `C` Chest |
| Stack Amplifier | `ADA` / `DSD` / `ADA` | `A` Amethyst Shard, `D` Diamond, `S` Shulker Shell |
| Stomping Horse Armor | `OAO` / `IHI` / `OIO` | `O` Obsidian, `A` Anvil, `I` Iron Ingot, `H` Iron Horse Armor |

### Configuration

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

### Development and building

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

### License

The mod metadata declares the MIT license.

---

## 简体中文

一个受《泰拉瑞亚》启发的 Minecraft 模组，为冒险加入永久物品栏升级、探索药水和践踏马铠。

### 运行环境

| 项目 | 版本 |
| --- | --- |
| Minecraft | 1.21.1 |
| NeoForge | 21.1.264 或更高的兼容版本 |
| Java | 21 |
| Mod ID | `terrautils` |

安装时，将构建得到的 JAR 放入游戏实例的 `mods` 目录。多人游戏需要在服务端和客户端安装此模组。

### 功能

#### 永久升级

手持升级道具右键使用，消耗道具并永久提升当前玩家的对应能力；升级等级在死亡后保留。

| 道具 | 每次升级的效果 | 上限 |
| --- | --- | --- |
| 扩容挎包 | 增加一行额外背包，提供 9 格存储空间 | 4 行，共 36 格 |
| 快捷栏扩展器 | 增加一行可轮换的额外快捷栏 | 3 行，共 27 格 |
| 堆叠增幅器 | 将可堆叠物品的堆叠上限翻倍 | 3 级，单组最多 99 个 |

原本不可堆叠的物品仍不可堆叠。堆叠升级作用于玩家物品栏和模组的额外存储空间。

物品栏满时，拾取的物品会尝试存入额外背包。额外存储中的物品在死亡时遵循 `keepInventory` 规则；未开启保留物品栏时会掉落，并遵循消失诅咒规则。

当前代码还注册了额外快捷栏槽位升级道具 `terrautils:hotbar_slots_upgrade`，最多解锁 4 格，依次对应 `0`、`-`、`=` 和反引号键。目前尚未提供该道具的合成配方、独立物品模型和本地化文本。

#### 探索与战斗药水

在酿造台中使用粗制的药水作为基础：

| 药水 | 酿造材料 | 效果 | 默认持续时间 |
| --- | --- | --- | --- |
| 洞穴探险药水 | 粗金 | 高亮附近矿石，默认扫描半径 24 格 | 3 分钟 |
| 寻宝药水 | 指南针 | 高亮附近箱子、木桶、潜影盒、可疑方块、饰纹陶罐、刷怪笼和宝库等目标，默认扫描半径 48 格 | 3 分钟 |
| 狱火药水 | 火焰弹 | 对周围目标造成持续范围伤害，I 级默认半径 4 格，每秒伤害 3 点 | 3 分钟 |

三种药水均可加入红石粉延长至 8 分钟。狱火药水还可加入荧石粉制成 II 级药水，持续 1 分 30 秒。

#### 践踏马铠

给马装备践踏马铠后，骑乘落地可触发范围伤害与击退，并保护马和骑手免受摔落伤害。

默认至少下落 3 格触发践踏，每格下落距离提供 2 点基础伤害，基础伤害上限为 40 点；实际伤害随目标与落点的距离衰减。默认不会伤害其他玩家。

### 默认按键

| 按键 | 操作 |
| --- | --- |
| `B` | 打开扩展物品栏 |
| `R` | 轮换快捷栏 |
| `Shift` + `R` | 反向轮换快捷栏 |

也可以通过物品栏界面的扩展按钮打开额外存储。`B` 和 `R` 可在游戏的按键设置中调整。

### 合成配方

以下图案均在工作台中合成，每次产出 1 个道具。

| 道具 | 图案（从上到下） | 材料说明 |
| --- | --- | --- |
| 扩容挎包 | `LDL` / `LCL` / `LLL` | `L` 皮革，`D` 钻石，`C` 箱子 |
| 快捷栏扩展器 | `GRG` / `ECE` / `GRG` | `G` 金锭，`R` 红石粉，`E` 末影珍珠，`C` 箱子 |
| 堆叠增幅器 | `ADA` / `DSD` / `ADA` | `A` 紫水晶碎片，`D` 钻石，`S` 潜影壳 |
| 践踏马铠 | `OAO` / `IHI` / `OIO` | `O` 黑曜石，`A` 铁砧，`I` 铁锭，`H` 铁马铠 |

### 配置

首次启动后，通用配置位于游戏实例的 `config/terrautils-common.toml`。

| 配置项 | 默认值 | 含义 |
| --- | --- | --- |
| `potions.spelunkerRadius` | `24` | 洞穴探险扫描半径 |
| `potions.treasureRadius` | `48` | 寻宝扫描半径 |
| `potions.infernoDamage` | `3.0` | 狱火 I 级每秒伤害 |
| `potions.infernoRadius` | `4.0` | 狱火 I 级范围半径 |
| `stomping_horse_armor.minFallDistance` | `3.0` | 践踏触发所需的最小下落距离 |
| `stomping_horse_armor.damagePerBlock` | `2.0` | 每格下落距离提供的践踏伤害 |
| `stomping_horse_armor.maxDamage` | `40.0` | 践踏基础伤害上限 |
| `stomping_horse_armor.hurtsPlayers` | `false` | 践踏是否伤害其他玩家 |

### 开发与构建

安装 JDK 21 后，在项目根目录使用自带的 Gradle Wrapper。首次构建需要联网下载 Gradle 和依赖。

Windows PowerShell：

```powershell
# 构建模组
.\gradlew.bat build

# 启动开发客户端
.\gradlew.bat runClient

# 启动开发服务端
.\gradlew.bat runServer

# 运行 GameTest
.\gradlew.bat runGameTestServer
```

Linux / macOS 可使用 `./gradlew` 执行相同任务。

构建产物位于 `build/libs/`，当前版本对应 `terrautils-1.0.0.jar`。版本、名称和加载器版本等设置见 [gradle.properties](gradle.properties)。

### 许可证

项目模组元数据声明的许可证为 MIT。
