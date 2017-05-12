package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.entity.Entity;

/**
 * Interface defining behavior for a function which alters the items resulting from an ILootEntry.
 * 
 * @author Jikoo
 */
public interface IFunction extends IConditional {

    public void modify(LootData lootData, Entity entity, int looting);

    public boolean isVariable();

}
