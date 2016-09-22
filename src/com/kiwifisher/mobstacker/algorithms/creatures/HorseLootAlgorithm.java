package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.ArrayList;
import java.util.List;

import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

public class HorseLootAlgorithm extends AnimalLootAlgorithm {

    public HorseLootAlgorithm() {
        /*
         * Future note: In 1.11, Skeletal/Undead horses drop the same quantity of their material as
         * horses do leather. They'll probably be a separate entity though. Either way, no need to
         * override getRandomLoot then.
         */
        this.getLootArray().add(new LootBuilder(Material.LEATHER).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {

        if (!(entity instanceof Horse)) {
            return new ArrayList<>();
        }

        Horse horse = (Horse) entity;

        if (!horse.isAdult()) {
            return new ArrayList<>();
        }

        switch (horse.getVariant()) {
        case SKELETON_HORSE:
            ArrayList<ItemStack> skeletonDrops = new ArrayList<>();
            this.addDrops(skeletonDrops, Material.BONE, (short) 0, numberOfMobs);
            return skeletonDrops;
        case UNDEAD_HORSE:
            ArrayList<ItemStack> undeadDrops = new ArrayList<>();
            this.addDrops(undeadDrops, Material.ROTTEN_FLESH, (short) 0, numberOfMobs);
            return undeadDrops;
        case DONKEY:
        case HORSE:
        case MULE:
        default:
            return super.getRandomLoot(entity, numberOfMobs, playerKill, looting);
        }

    }
}
