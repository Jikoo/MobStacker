package com.kiwifisher.mobstacker.loot.impl;

import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.entity.Entity;

/**
 * IFunction implementation for applying additional drop quantities based on looting.
 * 
 * @author Jikoo
 */
public class FunctionLootingBonus extends Function {

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

    @Override
    public Map<String, Object> serialize() {
        FunctionLootingBonus defaults = new FunctionLootingBonus();
        Map<String, Object> serialization = super.serialize();

        if (this.minimumBonus != defaults.getMinimumBonus()) {
            serialization.put("minimumBonus", this.minimumBonus);
        }
        if (this.maximumBonus != defaults.getMaximumBonus()) {
            serialization.put("maximumBonus", this.maximumBonus);
        }

        return serialization;
    }

    public FunctionLootingBonus deserialize(Map<String, Object> serialization) {
        FunctionLootingBonus function = new FunctionLootingBonus();

        SerializationUtils.load(function, Integer.class, "minimumBonus", serialization);
        SerializationUtils.load(function, Integer.class, "maximumBonus", serialization);
        SerializationUtils.loadList(function, ICondition.class, "conditions", serialization);

        return function;
    }

}
