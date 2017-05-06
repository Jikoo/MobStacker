package com.kiwifisher.mobstacker.loot.impl;

import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.entity.Entity;
import org.bukkit.material.Colorable;

/**
 * Function implementation for setting data to match a sheep (or any Colorable)'s drop data.
 * 
 * @author Jikoo
 */
public class FunctionMatchSheepWool extends Function {

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
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = super.serialize();

        if (this.invert) {
            serialization.put("invert", this.invert);
        }

        return serialization;
    }

    public FunctionMatchSheepWool deserialize(Map<String, Object> serialization) {
        FunctionMatchSheepWool function = new FunctionMatchSheepWool();

        SerializationUtils.load(function, Boolean.class, "invert", serialization);
        SerializationUtils.loadList(function, ICondition.class, "conditions", serialization);

        return function;
    }

}
