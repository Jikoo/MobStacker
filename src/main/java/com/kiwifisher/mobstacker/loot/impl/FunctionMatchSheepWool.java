package com.kiwifisher.mobstacker.loot.impl;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.LootData;

import org.bukkit.entity.Entity;
import org.bukkit.material.Colorable;

/**
 * Function implementation for setting data to match a sheep (or any Colorable)'s drop data.
 * 
 * @author Jikoo
 */
public class FunctionMatchSheepWool extends Function {

    @Expose
    private boolean invert;

    public FunctionMatchSheepWool() {
        this.invert = false;
    }

    public boolean getInvert() {
        return invert;
    }

    public void setInvert(Boolean invert) {
        this.invert = invert;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        if (!(entity instanceof Colorable)) {
            return;
        }

        if (invert) {
            lootData.setData(((Colorable) entity).getColor().getDyeData());
        } else {
            lootData.setData(((Colorable) entity).getColor().getWoolData());
        }
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.invert == ((FunctionMatchSheepWool) obj).invert;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return String.format("%s,invert=%s)", superString.substring(0, superString.length() - 1),
                this.invert);
    }

}
