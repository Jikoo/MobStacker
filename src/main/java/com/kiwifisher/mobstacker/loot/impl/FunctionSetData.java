package com.kiwifisher.mobstacker.loot.impl;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

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

    public short getMinimum() {
        return minimum;
    }

    public void setMinimum(Short minimum) {
        this.minimum = minimum;
    }

    public short getMaximum() {
        return maximum;
    }

    public void setMaximum(Short maximum) {
        this.maximum = maximum;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        if (isVariable()) {
            lootData.setData((short) ThreadLocalRandom.current().nextInt(minimum, maximum + 1));
        } else {
            lootData.setData(getMinimum());
        }
    }

    @Override
    public boolean isVariable() {
        return minimum < maximum;
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

        SerializationUtils.load(function, Short.class, "minimum", serialization);
        SerializationUtils.load(function, Short.class, "minimum", serialization);
        SerializationUtils.loadList(function, ICondition.class, "conditions", serialization);

        return function;
    }

}
