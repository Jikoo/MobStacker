package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.LootData;

import org.bukkit.entity.Entity;

/**
 * IFunction implementation for applying additional drop quantities based on looting.
 * 
 * @author Jikoo
 */
public class FunctionLootingBonus extends Function {

    @Expose
    private int minimumBonus, maximumBonus;

    public FunctionLootingBonus() {
        this.minimumBonus = 0;
        this.maximumBonus = 1;
    }

    public int getMinimumBonus() {
        return minimumBonus;
    }

    public void setMinimumBonus(Integer minimumBonus) {
        this.minimumBonus = minimumBonus;
    }

    public int getMaximumBonus() {
        return maximumBonus;
    }

    public void setMaximumBonus(Integer maximumBonus) {
        this.maximumBonus = maximumBonus;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        lootData.setMinimumQuantity(lootData.getMinimumQuantity() + this.getMinimumBonus() * looting);
        lootData.setMaximumQuantity(lootData.getMaximumQuantity() + this.getMaximumBonus() * looting);
    }

    @Override
    public boolean isVariable() {
        return false;
    }

}
