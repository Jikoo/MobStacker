package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;
import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;

import org.bukkit.Material;

public class ChickenLootAlgorithm extends AnimalLootAlgorithm {

    public ChickenLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(
                LootUtil.getCookableFunction(Material.RAW_CHICKEN, Material.COOKED_CHICKEN))
                        .withMinimum(1).withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.FEATHER).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
    }

}
