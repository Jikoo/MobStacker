package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;

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

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
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

        if (serialization.containsKey("minimum")) {
            Object minimum = serialization.get("minimum");
            if (short.class.isAssignableFrom(minimum.getClass())) {
                condition.setMinimum((short) minimum);
            }
        }

        if (serialization.containsKey("maximum")) {
            Object maximum = serialization.get("maximum");
            if (short.class.isAssignableFrom(maximum.getClass())) {
                condition.setMaximum((short) maximum);
            }
        }

        return condition;
    }

}
