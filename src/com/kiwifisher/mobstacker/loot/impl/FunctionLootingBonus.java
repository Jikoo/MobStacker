package com.kiwifisher.mobstacker.loot.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;

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

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        lootData.setMinimumQuantity(lootData.getMinimumQuantity() + this.getMinimumBonus() * looting);
        lootData.setMaximumQuantity(lootData.getMaximumQuantity() + this.getMaximumBonus() * looting);
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    public int getMinimumBonus() {
        return minimumBonus;
    }

    public void setMinimumBonus(int minimumBonus) {
        this.minimumBonus = minimumBonus;
    }

    public int getMaximumBonus() {
        return maximumBonus;
    }

    public void setMaximumBonus(int maximumBonus) {
        this.maximumBonus = maximumBonus;
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

        if (serialization.containsKey("conditions")) {
            Object conditions = serialization.get("conditions");
            if (conditions instanceof List) {
                List<ICondition> newConditions = new ArrayList<>();
                List<?> conditionList = (List<?>) conditions;
                for (Object condition : conditionList) {
                    if (condition instanceof ICondition) {
                        newConditions.add((ICondition) condition);
                    }
                }
                if (!newConditions.isEmpty()) {
                    function.setConditions(newConditions);
                }
            }
        }

        if (serialization.containsKey("minimumBonus")) {
            Object minimumBonus = serialization.get("minimumBonus");
            if (int.class.isAssignableFrom(minimumBonus.getClass())) {
                function.setMinimumBonus((int) minimumBonus);
            }
        }

        if (serialization.containsKey("maximumBonus")) {
            Object maximumBonus = serialization.get("maximumBonus");
            if (int.class.isAssignableFrom(maximumBonus.getClass())) {
                function.setMaximumBonus((int) maximumBonus);
            }
        }

        return function;
    }

}
