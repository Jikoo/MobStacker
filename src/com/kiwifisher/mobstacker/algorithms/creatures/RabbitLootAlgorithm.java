package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.CookableLoot;
import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.RareLoot;

import org.bukkit.Material;

public class RabbitLootAlgorithm extends AnimalLootAlgorithm {

    public RabbitLootAlgorithm() {
        this.getLootArray().add(new CookableLoot(Material.RABBIT, Material.COOKED_RABBIT, 0, 1, false));
        this.getLootArray().add(new Loot(Material.RABBIT_HIDE, 1));
        this.getLootArray().add(new RareLoot(Material.RABBIT_FOOT, 0.1) {
            @Override
            public double getDropChance(int looting) {
                // Rabbit foot drop chance is actually +3% per level, RareLoot adds +1% per level.
                return Math.min(1, super.getDropChance(looting) + 0.02 * looting);
            }
        });
    }

}
