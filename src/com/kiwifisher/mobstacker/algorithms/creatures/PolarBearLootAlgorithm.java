package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * LootAlgorithm for Polar Bears.
 * 
 * @author Jikoo
 */
public class PolarBearLootAlgorithm extends AnimalLootAlgorithm {

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        List<ItemStack> drops = new ArrayList<>();

        ThreadLocalRandom random = ThreadLocalRandom.current();

        /*
         * Polar bears drop either fish or salmon with a 75% bias towards fish. To keep drops fair
         * and even, we'll handle it similarly to guardians.
         */
        // TODO: Fix math
        int max = numberOfMobs * (2 + looting) * 100;

        int fish = random.nextInt(max / 75);

        int salmon = random.nextInt(max / 25 - fish);

        fish = fish * 75 / 100;
        salmon = salmon * 25 / 100;

        this.addDrops(drops, Material.RAW_FISH, (short) 0, fish);
        this.addDrops(drops, Material.RAW_FISH, (short) 1, salmon);

        return drops;
    }

}
