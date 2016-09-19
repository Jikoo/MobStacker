package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.RareLoot;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.inventory.ItemStack;

public class GuardianLootAlgorithm extends LootAlgorithm {

    public GuardianLootAlgorithm() {
        this.getLootArray().add(new Loot(Material.PRISMARINE_SHARD, 0, 2));
        this.getLootArray().add(new RareLoot(Material.RAW_FISH) {
            @Override
            public short getData(Entity entity) {
                double random = ThreadLocalRandom.current().nextDouble();
                if (random < 0.13) {
                    // 13% chance of pufferfish
                    return 3;
                }
                if (random < 0.15) {
                    // 2% chance of clownfish
                    return 2;
                }
                if (random < 0.40) {
                    // 25% chance of salmon
                    return 1;
                }
                // 60% chance of normal fish
                return 0;
            }
        });
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 10 * numberOfMobs;
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        List<ItemStack> drops = super.getRandomLoot(entity, numberOfMobs, playerKill, looting);

        ThreadLocalRandom random = ThreadLocalRandom.current();

        /*
         * Fiddly math: Guardians have a 40% chance of dropping a fish. If no fish is dropped, there
         * is a 40% chance of dropping a prismarine crystal. To do the percent drop chance, we
         * follow the same principal as the main loot algorithm for a percent chance with a chance
         * of 2/5. However, the percent chance for crystals has the total fish dropped subtracted
         * from it.
         */
        // TODO: wiki is unclear on actual drop chances - elder guardian and guardian pages don't match, read NMS
        // TODO: Fix math
        int max = numberOfMobs * 5 / 2 * looting;

        int fish = random.nextInt(max);

        int crystals = random.nextInt(max - fish);

        fish = fish * 2 / 5;
        crystals = crystals * 2 / 5;

        this.addDrops(drops, Material.RAW_FISH, (short) 0, fish);
        this.addDrops(drops, Material.PRISMARINE_CRYSTALS, (short) 0, crystals);

        if (!playerKill || !(entity instanceof Guardian)) {
            return drops;
        }

        Guardian guardian = (Guardian) entity;
        if (guardian.isElder()) {
            this.addDrops(drops, Material.SPONGE, (short) 1, numberOfMobs);
        }

        return drops;
    }

}
