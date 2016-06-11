package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class EndermanLootAlgorithm extends LootAlgorithm {

    public EndermanLootAlgorithm() {
        this.getLootArray().add(new Loot(Material.ENDER_PEARL, 0, 1));
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 5 * numberOfMobs;
    }

}
