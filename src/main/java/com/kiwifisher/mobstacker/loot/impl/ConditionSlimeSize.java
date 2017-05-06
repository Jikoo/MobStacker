package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Slime;

/**
 * Condition requiring a Slime to be a certain size or within a range of sizes.
 * 
 * @author Jikoo
 */
public class ConditionSlimeSize implements ICondition {

    private int minimum, maximum;
    public ConditionSlimeSize() {
        this.minimum = 0;
        this.maximum = 255;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(Integer minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(Integer maximum) {
        this.maximum = maximum;
    }

    @Override
    public boolean test(Entity entity) {
        if (!(entity instanceof Slime)) {
            return false;
        }
        Slime slime = (Slime) entity;
        if (slime.getSize() < this.getMinimum()) {
            return false;
        }
        if (this.getMinimum() >= this.getMaximum()) {
            return slime.getSize() == this.getMinimum();
        }
        return slime.getSize() <= this.getMaximum();
    }

    @Override
    public Map<String, Object> serialize() {
        ConditionSlimeSize defaults = new ConditionSlimeSize();
        Map<String, Object> serialization = new HashMap<>();

        if (this.minimum != defaults.getMinimum()) {
            serialization.put("minimum", this.minimum);
        }
        if (this.maximum != defaults.getMaximum()) {
            serialization.put("maximum", this.maximum);
        }

        return serialization;
    }

    public ConditionSlimeSize deserialize(Map<String, Object> serialization) {
        ConditionSlimeSize condition = new ConditionSlimeSize();

        SerializationUtils.load(condition, Short.class, "minimum", serialization);
        SerializationUtils.load(condition, Short.class, "maximum", serialization);

        return condition;
    }

}
