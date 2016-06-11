package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.CookableLoot;

import org.bukkit.Material;

public class PigLootAlgorithm  extends AnimalLootAlgorithm {

    public PigLootAlgorithm() {
        this.getLootArray().add(new CookableLoot(Material.PORK, Material.GRILLED_PORK, 1, 3));
    }

}
