package com.kiwifisher.mobstacker.algorithms.creatures;

import java.util.List;

import com.kiwifisher.mobstacker.algorithms.LootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.loot.Loot;
import com.kiwifisher.mobstacker.algorithms.loot.LootBuilder;
import com.kiwifisher.mobstacker.algorithms.loot.LootUtil;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class SkeletonLootAlgorithm extends LootAlgorithm {

    private final Loot arrow;
    private final Loot coal;
    private final Loot witherSkull;

    public SkeletonLootAlgorithm() {
        this.getLootArray().add(new LootBuilder(Material.BONE).withMaximum(2)
                .withAdditionalLootingResults().toLoot());
        this.arrow = new LootBuilder(Material.ARROW).withMaximum(2).withAdditionalLootingResults().toLoot();
        this.coal = new LootBuilder(Material.COAL).withMinimum(-1).withAdditionalLootingResults().toLoot();
        this.witherSkull = new LootBuilder(Material.SKULL_ITEM).withData((short) 1)
                .withPlayerKillRequired().withDropChance(LootUtil.RARE_LOOT_CHANCE)
                .withLootingDropChanceModifier(LootUtil.RARE_LOOT_MODIFER).toLoot();
    }

    @Override
    public int getExp(Entity entity, int numberOfMobs) {
        return 5 * numberOfMobs;
    }

    @Override
    public List<ItemStack> getRandomLoot(Entity entity, int numberOfMobs, boolean playerKill, int looting) {
        List<ItemStack> drops = super.getRandomLoot(entity, numberOfMobs, playerKill, looting);

        if (entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
            int amount = this.getRandomQuantity(coal, numberOfMobs, looting);
            this.addDrops(drops, coal.getMaterial(entity), coal.getData(entity), amount);

            if (playerKill) {
                amount = this.getRandomQuantity(witherSkull, numberOfMobs, looting);
                this.addDrops(drops, witherSkull.getMaterial(entity), witherSkull.getData(entity), amount);
            }

            return drops;
        }

        int amount = this.getRandomQuantity(arrow, numberOfMobs, looting);
        this.addDrops(drops, arrow.getMaterial(entity), arrow.getData(entity), amount);

        // Strays drop arrows of slowness on player kill.
        if (!playerKill || entity instanceof Skeleton && ((Skeleton) entity).getSkeletonType() != SkeletonType.STRAY) {
            return drops;
        }

        amount = this.getRandomQuantity(0, 1, (2D * looting + 1) / (2D * looting + 2), numberOfMobs);

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
