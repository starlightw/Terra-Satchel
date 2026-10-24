package com.starlight.terrautils.client.render;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/** Finds blocks around the player that a highlight effect should outline. Runs on the client thread. */
public final class HighlightScanner {
    public record Highlight(AABB box, int color) {}

    /** Block -> outline color; 0 means "not highlighted". Cached since tag lookups are per block, not per state. */
    private static final Map<Block, Integer> ORE_COLORS = new IdentityHashMap<>();
    private static final Map<Block, Integer> TREASURE_COLORS = new IdentityHashMap<>();

    private static final List<Map.Entry<TagKey<Block>, Integer>> ORE_TAG_COLORS = List.of(
            Map.entry(Tags.Blocks.ORES_DIAMOND, 0x4AEDD9),
            Map.entry(Tags.Blocks.ORES_EMERALD, 0x17DD62),
            Map.entry(Tags.Blocks.ORES_GOLD, 0xFCEE4B),
            Map.entry(Tags.Blocks.ORES_IRON, 0xD8AF93),
            Map.entry(Tags.Blocks.ORES_COPPER, 0xE77C56),
            Map.entry(Tags.Blocks.ORES_REDSTONE, 0xFF2020),
            Map.entry(Tags.Blocks.ORES_LAPIS, 0x2050FF),
            Map.entry(Tags.Blocks.ORES_COAL, 0x5A5A5A),
            Map.entry(Tags.Blocks.ORES_QUARTZ, 0xF0EBE4),
            Map.entry(Tags.Blocks.ORES_NETHERITE_SCRAP, 0x8B5A4A));
    private static final int OTHER_ORE_COLOR = 0xFF66FF;

    private HighlightScanner() {}

    public static void clearCaches() {
        ORE_COLORS.clear();
        TREASURE_COLORS.clear();
    }

    public static int oreColor(BlockState state) {
        return ORE_COLORS.computeIfAbsent(state.getBlock(), block -> {
            if (!state.is(Tags.Blocks.ORES)) {
                return 0;
            }
            for (Map.Entry<TagKey<Block>, Integer> entry : ORE_TAG_COLORS) {
                if (state.is(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return OTHER_ORE_COLOR;
        });
    }

    public static int treasureColor(BlockState state) {
        return TREASURE_COLORS.computeIfAbsent(state.getBlock(), block -> {
            if (block instanceof ChestBlock) return 0xFFC800;
            if (block instanceof BarrelBlock) return 0xC08040;
            if (block instanceof ShulkerBoxBlock) return 0xC060FF;
            if (block instanceof BrushableBlock) return 0xF0D090;
            if (block instanceof DecoratedPotBlock) return 0xE07050;
            if (block == Blocks.SPAWNER || block == Blocks.TRIAL_SPAWNER) return 0xFF3030;
            if (block == Blocks.VAULT) return 0x40E0FF;
            return 0;
        });
    }

    /** Scans a cube of {@code radius} around {@code center}, collecting outlines for blocks with a non-zero color. */
    public static List<Highlight> scan(ClientLevel level, BlockPos center, int radius, ColorFunction colors) {
        List<Highlight> result = new ArrayList<>();
        Predicate<BlockState> matches = state -> colors.color(state) != 0;
        int minSecX = SectionPos.blockToSectionCoord(center.getX() - radius);
        int maxSecX = SectionPos.blockToSectionCoord(center.getX() + radius);
        int minSecZ = SectionPos.blockToSectionCoord(center.getZ() - radius);
        int maxSecZ = SectionPos.blockToSectionCoord(center.getZ() + radius);
        int minY = Math.max(level.getMinBuildHeight(), center.getY() - radius);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, center.getY() + radius);
        long radiusSq = (long) radius * radius;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int sx = minSecX; sx <= maxSecX; sx++) {
            for (int sz = minSecZ; sz <= maxSecZ; sz++) {
                if (!level.hasChunk(sx, sz)) {
                    continue;
                }
                LevelChunk chunk = level.getChunk(sx, sz);
                for (int sy = SectionPos.blockToSectionCoord(minY); sy <= SectionPos.blockToSectionCoord(maxY); sy++) {
                    int index = chunk.getSectionIndexFromSectionY(sy);
                    if (index < 0 || index >= chunk.getSectionsCount()) {
                        continue;
                    }
                    LevelChunkSection section = chunk.getSection(index);
                    if (section.hasOnlyAir() || !section.maybeHas(matches)) {
                        continue;
                    }
                    int baseX = SectionPos.sectionToBlockCoord(sx);
                    int baseY = SectionPos.sectionToBlockCoord(sy);
                    int baseZ = SectionPos.sectionToBlockCoord(sz);
                    for (int y = 0; y < 16; y++) {
                        int wy = baseY + y;
                        if (wy < minY || wy > maxY) continue;
                        for (int z = 0; z < 16; z++) {
                            int wz = baseZ + z;
                            for (int x = 0; x < 16; x++) {
                                int wx = baseX + x;
                                long dx = wx - center.getX(), dy = wy - center.getY(), dz = wz - center.getZ();
                                if (dx * dx + dy * dy + dz * dz > radiusSq) continue;
                                BlockState state = section.getBlockState(x, y, z);
                                int color = colors.color(state);
                                if (color != 0) {
                                    pos.set(wx, wy, wz);
                                    result.add(new Highlight(outline(level, pos, state), color));
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private static AABB outline(ClientLevel level, BlockPos pos, BlockState state) {
        VoxelShape shape = state.getShape(level, pos);
        AABB box = shape.isEmpty() ? new AABB(0, 0, 0, 1, 1, 1) : shape.bounds();
        return box.move(pos).inflate(0.002);
    }

    @FunctionalInterface
    public interface ColorFunction {
        int color(BlockState state);
    }
}
