package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.Loot;

import org.bukkit.Material;

public class SquidLootAlgorithm extends AnimalLootAlgorithm {

    public SquidLootAlgorithm() {
        this.getLootArray().add(new Loot(Material.INK_SACK, 1, 3));
    }

}
