package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.List;

import com.kiwifisher.mobstacker.algorithms.Loot;
import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.RareLoot;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class SkeletonLootAlgorithm extends LootAlgorithm {

    private final Loot witherSkull;

    public SkeletonLootAlgorithm() {
        this.getLootArray().add(new Loot(Material.BONE, 0, 2));
        this.getLootArray().add(new Loot(Material.ARROW, 0, 2) {
            @Override
            public Material getMaterial(Entity entity) {
                if (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
                    return Material.COAL;
                }
                return super.getMaterial(entity);
            }
        });
        this.witherSkull = new RareLoot(Material.SKULL_ITEM, (short) 1);
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 5 * numberOfMobs;
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        List<ItemStack> drops = super.getRandomLoot(entity, numberOfMobs, playerKill, looting);

        if (playerKill && entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
            int amount = this.getRandomQuantity(witherSkull, numberOfMobs, looting);
            this.addDrops(drops, witherSkull.getMaterial(entity), witherSkull.getData(entity), amount);
            return drops;
        }

        // Strays drop arrows of slowness.
        if (!playerKill || entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() != SkeletonType.STRAY) {
            return drops;
        }

        int amount = this.getRandomQuantity(0, 1, (2 * looting + 1) / (2 * looting + 2), numberOfMobs);

        ItemStack arrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta meta = (PotionMeta) arrow.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.SLOWNESS));
        arrow.setItemMeta(meta);

        while (amount > 0) {
            if (amount > 64) {
                ItemStack clone = arrow.clone();
                clone.setAmount(64);
                drops.add(clone);
                amount -= 64;
            } else {
                arrow.setAmount(amount);
                drops.add(arrow);
                amount = 0;
            }
        }

        return drops;
    }

}
