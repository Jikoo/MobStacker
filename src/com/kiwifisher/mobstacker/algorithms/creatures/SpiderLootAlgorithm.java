package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.Loot;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class SpiderLootAlgorithm extends LootAlgorithm {

    private final List<Loot> dropArrayList = new ArrayList<>();

    public SpiderLootAlgorithm() {
        dropArrayList.add(new LootBuilder(Material.STRING).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        dropArrayList.add(new LootBuilder(Material.SPIDER_EYE).withMinimum(-1)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobsWorth) {
        return 5 * numberOfMobsWorth;
    }

}

