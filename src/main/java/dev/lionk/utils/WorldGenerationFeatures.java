package dev.lionk.utils;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class WorldGenerationFeatures {
    public static void buildTree(Block stem, Block leaves, int x, int y, int z, @NotNull GenerationUnit unit) {
        Random r = WorldGenerator.getRandom(x, z);
        int maxHeight = y + 5+ r.nextInt(1, 4); //Get height for Mushroom

        //Generate Stem
        for (int i = y;i<=maxHeight;i++){
            unit.modifier().setBlock(x, i, z, stem);
        }

        //Generate Top
        for(int i = -1;i<=1;i++){
            for (int j = -1;j<=1;j++){
                int finalI = i;
                int finalJ = j;
                unit.fork(setter -> setter.setBlock(x+ finalI, maxHeight+1, z+ finalJ, leaves));
            }
        }

        //Generate Sides
        for (int y2 = 0;y2<r.nextInt(4, 7);y2++){
            for(int i = -2;i<=2;i++){
                for (int j = -2;j<=2;j++){
                    if ((Math.abs(i)==2 && Math.abs(j)<=1)||
                            Math.abs(i)<2&&Math.abs(j)==2) {
                        int finalI = i;
                        int finalY = y2;
                        int finalJ = j;
                        unit.fork(setter -> {
                            setter.setBlock(x+ finalI, maxHeight- finalY, z+ finalJ, leaves);
                        });
                    }
                }
            }
        }

    }
}
