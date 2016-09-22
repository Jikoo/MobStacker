package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;

public class CowLootAlgorithm extends AnimalLootAlgorithm {

    public CowLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(
                LootUtil.getCookableFunction(Material.RAW_BEEF, Material.COOKED_BEEF))
                        .withMinimum(1).withMaximum(3).withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.LEATHER).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
    }

}
