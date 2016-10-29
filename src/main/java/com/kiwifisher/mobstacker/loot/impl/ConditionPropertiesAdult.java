package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

/**
 * Condition requiring an Entity to be an adult or not ageable.
 * 
 * @author Jikoo
 */
public class ConditionPropertiesAdult implements ICondition {

    private boolean adult;

    public ConditionPropertiesAdult() {
        this.adult = true;
    }

    @Override
    public boolean test(Entity entity) {
        if (entity instanceof Ageable) {
            return adult == ((Ageable) entity).isAdult();
        }

        return adult;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();

        // Default value is true, prevent clutter.
        if (adult == false) {
            serialization.put("adult", false);
        }

        return serialization;
    }

    public static ConditionPropertiesAdult deserialize(Map<String, Object> serialization) {
        ConditionPropertiesAdult condition = new ConditionPropertiesAdult();

        if (serialization.containsKey("adult")) {
            Object adult = serialization.get("adult");
            if (boolean.class.isAssignableFrom(adult.getClass())) {
                condition.setAdult((boolean) adult);
            }
        }

        return condition;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

}
