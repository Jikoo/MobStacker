package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class IronGolemLootAlgorithm extends LootAlgorithm {

    public IronGolemLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.RED_ROSE).withMaximum(2).toLoot());
        this.getLootArray().add(new LootBuilder(Material.IRON_INGOT).withMinimum(3).withMaximum(5).toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 0;
    }

}
