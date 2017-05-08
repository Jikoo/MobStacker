package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.ICondition;

import org.bukkit.entity.Entity;

/**
 * ICondition implementation requiring the entity tested to be on (or, configurably, not on) fire.
 * 
 * @author Jikoo
 */
public class ConditionPropertiesOnFire implements ICondition {

    @Expose
    private boolean burning;

    public ConditionPropertiesOnFire() {
        this.burning = true;
    }

    public boolean getBurning() {
        return burning;
    }

    public void setBurning(Boolean burning) {
        this.burning = burning;
    }

    @Override
    public boolean test(Entity entity) {
        return entity != null && entity.getFireTicks() > 0 ? burning : !burning;
    }

}
