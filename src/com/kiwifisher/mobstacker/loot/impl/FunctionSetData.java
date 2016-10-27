package com.kiwifisher.mobstacker.loot.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;

import org.bukkit.entity.Entity;

/**
 * IFunction implementation for setting data to a number or range of numbers.
 * 
 * @author Jikoo
 */
public class FunctionSetData extends Function {

    private short minimum;
    private short maximum;

    public FunctionSetData() {
        this.minimum = 0;
        this.maximum = 0;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        if (isVariable()) {
            lootData.setData((short) ThreadLocalRandom.current().nextInt(minimum, maximum));
        } else {
            lootData.setData(getMinimum());
        }
    }

    @Override
    public boolean isVariable() {
        return minimum >= maximum;
    }

    public short getMinimum() {
        return minimum;
    }

    public void setMinimum(short minimum) {
        this.minimum = minimum;
    }

    public short getMaximum() {
        return maximum;
    }

    public void setMaximum(short maximum) {
        this.maximum = maximum;
    }

    @Override
    public Map<String, Object> serialize() {
        FunctionSetData defaults = new FunctionSetData();
        Map<String, Object> serialization = super.serialize();

        if (this.minimum != defaults.getMinimum()) {
            serialization.put("minimum", this.minimum);
        }
        if (this.maximum != defaults.getMaximum()) {
            serialization.put("maximum", this.maximum);
        }

        return serialization;
    }

    public FunctionSetData deserialize(Map<String, Object> serialization) {
        FunctionSetData function = new FunctionSetData();

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

        if (serialization.containsKey("minimum")) {
            Object minimum = serialization.get("minimum");
            if (short.class.isAssignableFrom(minimum.getClass())) {
                function.setMinimum((short) minimum);
            }
        }

        if (serialization.containsKey("maximum")) {
            Object maximum = serialization.get("maximum");
            if (short.class.isAssignableFrom(maximum.getClass())) {
                function.setMaximum((short) maximum);
            }
        }

        return function;
    }

}
