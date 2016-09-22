package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;

public class SquidLootAlgorithm extends AnimalLootAlgorithm {

    public SquidLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.INK_SACK).withMinimum(1).withMaximum(3)
                .withAdditionalLootingResults().toLoot());
    }

}
