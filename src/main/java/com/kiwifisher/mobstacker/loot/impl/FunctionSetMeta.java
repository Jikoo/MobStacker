package com.kiwifisher.mobstacker.loot.impl;

import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;
import com.kiwifisher.mobstacker.utils.SerializationUtils;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * IFunction implementation for setting the resulting stack's ItemMeta.
 * 
 * @author Jikoo
 */
public class FunctionSetMeta extends Function {

    private ItemMeta meta;

    public FunctionSetMeta() {}

    public ItemMeta getMeta() {
        return meta;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        lootData.setMeta(getMeta());
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = super.serialize();

        if (this.meta != null) {
            serialization.put("meta", this.meta);
        }

        return serialization;
    }

    public FunctionSetMeta deserialize(Map<String, Object> serialization) {
        FunctionSetMeta function = new FunctionSetMeta();

        SerializationUtils.load(function, ItemMeta.class, "meta", serialization);
        SerializationUtils.loadList(function, ICondition.class, "conditions", serialization);

        return function;
    }

}
