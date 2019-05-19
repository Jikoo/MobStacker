package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.Condition;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Condition for an entity being killed by a Player.
 *
 * @author Jikoo
 */
public class ConditionKilledByPlayer implements Condition {

    @Expose
    private boolean invert;

    public ConditionKilledByPlayer() {
        this.invert = false;
    }

    public boolean getInvert() {
        return invert;
    }

    public void setInvert(Boolean invert) {
        this.invert = invert;
    }

    @Override
    public boolean test(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        return (((LivingEntity) entity).getKiller() == null) == invert;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass())
                && this.invert == ((ConditionKilledByPlayer) obj).invert;
    }

    @Override
    public String toString() {
        return String.format("%s(invert=%s)", this.getClass().getName(), this.invert);
    }

}
