package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class SpiderLootAlgorithm extends LootAlgorithm {

    private final List<Loot> dropArrayList = new ArrayList<>();

    public SpiderLootAlgorithm() {
        dropArrayList.add(new Loot(Material.STRING, 0, 2));
        dropArrayList.add(new Loot(Material.SPIDER_EYE, 1, 1.0/3));
    }

    @Override
    public int getExp(Entity entity, int numberOfMobsWorth) {
        return 5 * numberOfMobsWorth;
    }

}

