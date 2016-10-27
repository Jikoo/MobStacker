package com.kiwifisher.mobstacker.loot.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;

import org.bukkit.entity.Entity;
import org.bukkit.material.Colorable;

/**
 * Function implementation for setting data to match a sheep (or any Colorable)'s drop data.
 * 
 * @author Jikoo
 */
public class FunctionMatchSheepWool extends Function {

    private boolean invert;

    public FunctionMatchSheepWool() {
        this.invert = false;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        if (!(entity instanceof Colorable)) {
            return;
        }

        if (invert) {
            lootData.setData(((Colorable) entity).getColor().getDyeData());
        } else {
            lootData.setData(((Colorable) entity).getColor().getWoolData());
        }
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    public boolean getInvert() {
        return invert;
    }

    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = super.serialize();

        if (this.invert) {
            serialization.put("invert", this.invert);
        }

        return serialization;
    }

    public FunctionMatchSheepWool deserialize(Map<String, Object> serialization) {
        FunctionMatchSheepWool function = new FunctionMatchSheepWool();

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

        if (serialization.containsKey("invert")) {
            Object invert = serialization.get("invert");
            if (boolean.class.isAssignableFrom(invert.getClass())) {
                function.setInvert((boolean) invert);
            }
        }

        return function;
    }

}
