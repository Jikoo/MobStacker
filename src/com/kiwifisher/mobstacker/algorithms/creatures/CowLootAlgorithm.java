package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.CookableLoot;
import com.kiwifisher.mobstacker.algorithms.Loot;

import org.bukkit.Material;

public class CowLootAlgorithm extends AnimalLootAlgorithm {

    public CowLootAlgorithm() {
        this.getLootArray().add(new CookableLoot(Material.RAW_BEEF, Material.COOKED_BEEF, 1, 1));
        this.getLootArray().add(new Loot(Material.LEATHER, 0, 2));
    }

}
