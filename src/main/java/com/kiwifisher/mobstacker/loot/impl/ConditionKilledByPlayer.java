package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.ICondition;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Condition for an entity being killed by a Player.
 * 
 * @author Jikoo
 */
public class ConditionKilledByPlayer implements ICondition {

    @Expose
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

}
