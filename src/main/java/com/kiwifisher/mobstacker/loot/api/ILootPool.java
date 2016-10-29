package com.kiwifisher.mobstacker.loot.api;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Interface defining the behavior of a pool of loot entries.
 * 
 * @author Jikoo
 */
public interface ILootPool extends IConditional, IRandomizable {

    public void roll(List<ItemStack> drops, Entity entity, int numberOfMobs, int looting);

}
