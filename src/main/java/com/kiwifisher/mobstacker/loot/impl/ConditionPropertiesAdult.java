package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

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

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
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

        SerializationUtils.load(condition, Boolean.class, "adult", serialization);

        return condition;
    }

}
