package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;

import org.bukkit.entity.Entity;

/**
 * ICondition implementation requiring the entity tested to be on (or, configurably, not on) fire.
 * 
 * @author Jikoo
 */
public class ConditionPropertiesOnFire implements ICondition {

    private boolean burning;

    public ConditionPropertiesOnFire() {
        this.burning = true;
    }

    public boolean getBurning() {
        return burning;
    }

    public void setBurning(boolean burning) {
        this.burning = burning;
    }

    @Override
    public boolean test(Entity entity) {
        return entity != null && entity.getFireTicks() > 0 ? burning : !burning;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();

        // Default value is true, prevent clutter.
        if (burning == false) {
            serialization.put("burning", burning);
        }

        return serialization;
    }

    public static ConditionPropertiesOnFire deserialize(Map<String, Object> serialization) {
        ConditionPropertiesOnFire condition = new ConditionPropertiesOnFire();

        if (serialization.containsKey("burning")) {
            Object burning = serialization.get("burning");
            if (boolean.class.isAssignableFrom(burning.getClass())) {
                condition.setBurning((boolean) burning);
            }
        }

        return condition;
    }

}
