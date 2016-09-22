package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * LootAlgorithm for Ghasts.
 * 
 * @author Jikoo
 */
public class GhastLootAlgorithm extends LootAlgorithm {

    public GhastLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.GHAST_TEAR)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.SULPHUR).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 5 * numberOfMobs;
    }

}
