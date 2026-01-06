package dev.lionk.utils;

import net.minestom.server.coordinate.Point;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Random;


public class WorldGenerator implements Generator {

    // Configuration
    private static final int FLOOR_HEIGHT = 64;
    private static final double TRANSITION_START_DISTANCE = 50.0;
    private static final double FULL_PURPLE_DISTANCE = 300.0;

    // Palettes
    private static final List<Block> BLACKSTONE_PALETTE = List.of(
            Block.BLACKSTONE,
            Block.POLISHED_BLACKSTONE,
            Block.POLISHED_BLACKSTONE_BRICKS,
            Block.CRACKED_POLISHED_BLACKSTONE_BRICKS
    );

    private static final List<Block> PURPLE_PALETTE = List.of(
            Block.CRIMSON_NYLIUM
    );

    private static final List<Block> DEKO_PALETTE = List.of(
            Block.CRIMSON_ROOTS,
            Block.CRIMSON_FUNGUS
    );

    @Override
    public void generate(@NotNull GenerationUnit unit) {
        Point start = unit.absoluteStart();
        Point end = unit.absoluteEnd();

        // 1. Fill the bottom of the world with generic stone/bedrock for optimization
        // This fills everything from the bottom of the chunk up to Y=63
        unit.modifier().fillHeight(0, FLOOR_HEIGHT, Block.STONE);
        //unit.modifier().setBlock(start.withY(0), end.withY(1), Block.BEDROCK);

        // 2. Iterate over the surface layer (X and Z) to place our custom blocks
        for (int x = start.blockX(); x < end.blockX(); x++) {
            for (int z = start.blockZ(); z < end.blockZ(); z++) {

                // Determine which block to place based on distance
                Block surfaceBlock = getBlockForLocation(x, z);

                // Set the block at the floor height
                unit.modifier().setBlock(x, FLOOR_HEIGHT, z, surfaceBlock);

                if (surfaceBlock.compare(Block.CRIMSON_NYLIUM)){
                    Random r = getRandom(x, z);
                    int i = r.nextInt(100);
                    if (i==11) WorldGenerationFeatures.buildTree(Block.CRIMSON_STEM, Block.NETHER_WART_BLOCK, x, FLOOR_HEIGHT+1, z, unit);
                    if (i < 10){
                        unit.modifier().setBlock(x, FLOOR_HEIGHT+1, z, DEKO_PALETTE.get(r.nextInt(DEKO_PALETTE.size())));
                    }
                }
            }
        }
    }

    public static Random getRandom(int x, int z){
        // We use a deterministic seed based on coordinates so the world
        // doesn't change when you reload chunks.
        long seed = (long) x * 341873128712L + (long) z * 132897987541L;
        return new Random(seed);
    }

    /**
     * Calculates the distance from spawn and selects a block based on probability.
     */
    private Block getBlockForLocation(int x, int z) {
        // Calculate 2D distance from (0,0)
        double distance = Math.sqrt(x * x + z * z);

        // Calculate probability of being purple (0.0 to 1.0)
        double purpleProbability = getProbability(distance);

        Random random = getRandom(x, z);

        if (random.nextDouble() < purpleProbability) {
            // Pick random purple block
            return PURPLE_PALETTE.get(random.nextInt(PURPLE_PALETTE.size()));
        } else {
            // Pick random blackstone block
            return BLACKSTONE_PALETTE.get(random.nextInt(BLACKSTONE_PALETTE.size()));
        }
    }

    /**
     * Math helper to determine gradient intensity.
     */
    private double getProbability(double distance) {
        if (distance <= TRANSITION_START_DISTANCE) return 0.0;
        if (distance >= FULL_PURPLE_DISTANCE) return 1.0;

        // Linear interpolation (Lerp)
        return (distance - TRANSITION_START_DISTANCE) / (FULL_PURPLE_DISTANCE - TRANSITION_START_DISTANCE);
    }
}

