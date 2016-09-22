package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class EndermanLootAlgorithm extends LootAlgorithm {

    public EndermanLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.ENDER_PEARL)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 5 * numberOfMobs;
    }

}
