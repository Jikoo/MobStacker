package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class SnowGolemLootAlgorithm extends LootAlgorithm {

    public SnowGolemLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.SNOW_BALL).withMaximum(15).toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 0;
    }

}
