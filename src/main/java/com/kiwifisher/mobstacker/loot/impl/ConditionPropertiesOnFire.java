package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.Condition;

import org.bukkit.entity.Entity;

/**
 * Condition implementation requiring the entity tested to be on (or, configurably, not on) fire.
 *
 * @author Jikoo
 */
public class ConditionPropertiesOnFire implements Condition {

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
        return (entity != null && entity.getFireTicks() > 0) == burning;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass())
                && this.burning == ((ConditionPropertiesOnFire) obj).burning;
    }

    @Override
    public String toString() {
        return String.format("%s(burning=%s)", this.getClass().getName(), this.burning);
    }

}
