package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * LootAlgorithm for Withers.
 * 
 * @author Jikoo
 */
public class WitherLootAlgorithm extends LootAlgorithm {

    public WitherLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.NETHER_STAR).withMinimum(1).toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 50 * numberOfMobs;
    }

}
