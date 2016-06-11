package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.CookableLoot;
import com.kiwifisher.mobstacker.algorithms.Loot;

import org.bukkit.Material;

public class ChickenLootAlgorithm extends AnimalLootAlgorithm {

    public ChickenLootAlgorithm() {
        this.getLootArray().add(new CookableLoot(Material.RAW_CHICKEN, Material.COOKED_CHICKEN, 1, 1));
        this.getLootArray().add(new Loot(Material.FEATHER, 0, 2));
    }

}
