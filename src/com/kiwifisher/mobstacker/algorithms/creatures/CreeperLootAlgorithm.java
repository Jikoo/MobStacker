package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class CreeperLootAlgorithm extends LootAlgorithm {

    public CreeperLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.SULPHUR).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 5 * numberOfMobs;
    }

}
