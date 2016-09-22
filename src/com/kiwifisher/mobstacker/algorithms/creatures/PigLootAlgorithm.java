package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;
import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;

import org.bukkit.Material;

public class PigLootAlgorithm  extends AnimalLootAlgorithm {

    public PigLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(
                LootUtil.getCookableFunction(Material.PORK, Material.GRILLED_PORK))
        .withMinimum(1).withMaximum(3).withAdditionalLootingResults().toLoot());
    }

}
