package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.Iterator;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.RareLoot;

import org.bukkit.Material;

public class PigZombieLootAlgorithm extends ZombieLootAlgorithm {

    public PigZombieLootAlgorithm() {
        for (Iterator<Loot> iterator = this.getLootArray().iterator(); iterator.hasNext();) {
            if (iterator.next() instanceof RareLoot) {
                iterator.remove();
            }
        }

        this.getLootArray().add(new Loot(Material.GOLD_NUGGET, 1));
        this.getLootArray().add(new RareLoot(Material.GOLD_INGOT));
        // TODO: sword?
    }

}
