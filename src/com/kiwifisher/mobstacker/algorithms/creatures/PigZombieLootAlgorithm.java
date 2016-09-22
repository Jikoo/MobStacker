package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.Iterator;

import com.kiwifisher.mobstacker.algorithms.loot.Loot;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;
import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;

import org.bukkit.Material;

public class PigZombieLootAlgorithm extends ZombieLootAlgorithm {

    public PigZombieLootAlgorithm() {
        for (Iterator<Loot> iterator = this.getLootArray().iterator(); iterator.hasNext();) {
            if (iterator.next().getPlayerKillRequired()) {
                // Player kill required means rare loot, only rare loot differs.
                iterator.remove();
            }
        }

        this.getLootArray().add(new LootBuilder(Material.GOLD_NUGGET).withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.GOLD_INGOT).withPlayerKillRequired()
                .withLootingDropChanceModifier(LootUtil.RARE_LOOT_MODIFER)
                .withDropChance(LootUtil.RARE_LOOT_CHANCE).toLoot());
        // TODO: sword?
    }

}
