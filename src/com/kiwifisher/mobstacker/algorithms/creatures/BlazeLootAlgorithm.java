package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class BlazeLootAlgorithm extends LootAlgorithm {

    public BlazeLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.BLAZE_ROD)
                .withAdditionalLootingResults().withPlayerKillRequired().toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 10 * numberOfMobs;
    }

}
