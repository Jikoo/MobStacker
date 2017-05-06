package com.kiwifisher.mobstacker.loot.impl;

import java.util.HashMap;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Condition for an entity being killed by a Player.
 * 
 * @author Jikoo
 */
public class ConditionKilledByPlayer implements ICondition {

    private boolean inverse;

    public ConditionKilledByPlayer() {
        this.inverse = false;
    }

    public boolean getInverse() {
        return inverse;
    }

    public void setInverse(Boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public boolean test(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        // instanceof essentially includes a null check, this is safe
        return ((LivingEntity) entity).getKiller() instanceof Player ? !inverse : inverse;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();

        // Default value is true, prevent clutter.
        if (inverse == true) {
            serialization.put("inverse", true);
        }

        return serialization;
    }

    public ConditionKilledByPlayer deserialize(Map<String, Object> serialization) {
        ConditionKilledByPlayer condition = new ConditionKilledByPlayer();

        SerializationUtils.load(condition, Boolean.class, "inverse", serialization);

        return condition;
    }

}
