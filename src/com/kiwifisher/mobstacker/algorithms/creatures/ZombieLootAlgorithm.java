package com.kiwifisher.mobstacker.algorithms.creatures;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;
import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;

import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public class ZombieLootAlgorithm extends LootAlgorithm {

    public ZombieLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.ROTTEN_FLESH).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.getLootArray().add(new LootBuilder(Material.IRON_INGOT)
                .withPlayerKillRequired().withDropChance(LootUtil.RARE_LOOT_CHANCE)
                .withLootingDropChanceModifier(LootUtil.RARE_LOOT_MODIFER).toLoot());
        this.getLootArray().add(new LootBuilder(Material.CARROT_ITEM)
                .withPlayerKillRequired().withDropChance(LootUtil.RARE_LOOT_CHANCE)
                .withLootingDropChanceModifier(LootUtil.RARE_LOOT_MODIFER).toLoot());
        this.getLootArray().add(new LootBuilder(Material.POTATO_ITEM)
                .withPlayerKillRequired().withDropChance(LootUtil.RARE_LOOT_CHANCE)
                .withLootingDropChanceModifier(LootUtil.RARE_LOOT_MODIFER).toLoot());
        // TODO: Allow randomized loots like this to work
//        this.getLootArray().add(new LootBuilder(new Function<Entity, Material>() {
//                    @Override
//                    public Material apply(Entity t) {
//                        switch(ThreadLocalRandom.current().nextInt(3)) {
//                        case 0:
//                            return Material.IRON_INGOT;
//                        case 1:
//                            return Material.CARROT_ITEM;
//                        case 2:
//                        default: // I like security, okay?
//                            return Material.POTATO_ITEM;
//                        }
//                    }
//                }).withPlayerKillRequired().withDropChance(LootUtil.RARE_LOOT_CHANCE)
//                .withLootingDropChanceModifier(LootUtil.RARE_LOOT_MODIFER).toLoot());
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        // Babies drop 12 exp.
        if (entity instanceof Ageable && !((Ageable) entity).isAdult()) {
            return 12 * numberOfMobs;
        }
        // Adults drop 5 exp per.
        return 5 * numberOfMobs;
    }

}
