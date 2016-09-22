package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;
import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;

import org.bukkit.Material;

public class RabbitLootAlgorithm extends AnimalLootAlgorithm {

    public RabbitLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(
                LootUtil.getCookableFunction(Material.RABBIT, Material.COOKED_RABBIT))
                        .withMaximum(1).toLoot());
        this.getLootArray().add(new LootBuilder(Material.RABBIT_HIDE).withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.RABBIT_FOOT).withDropChance(0.1)
                .withLootingDropChanceModifier(0.03).toLoot());
    }

}
