package com.kiwifisher.mobstacker.loot.api;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Interface defining behavior for a loot entry.
 * 
 * @author Jikoo
 */
public interface ILootEntry extends IConditional, IRandomizable {

    public int getWeight();

    public double getQuality();

    public Material getMaterial();

    public int getMinimumQuantity();

    public int getMaximumQuantity();

    public void generateLoot(List<ItemStack> stacks, int value, Entity entity, int looting);

}
