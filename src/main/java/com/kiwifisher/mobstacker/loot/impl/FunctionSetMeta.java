package com.kiwifisher.mobstacker.loot.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.LootData;

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

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        lootData.setMeta(getMeta());
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
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

        if (serialization.containsKey("conditions")) {
            Object conditions = serialization.get("conditions");
            if (conditions instanceof List) {
                List<ICondition> newConditions = new ArrayList<>();
                List<?> conditionList = (List<?>) conditions;
                for (Object condition : conditionList) {
                    if (condition instanceof ICondition) {
                        newConditions.add((ICondition) condition);
                    }
                }
                if (!newConditions.isEmpty()) {
                    function.setConditions(newConditions);
                }
            }
        }

        if (serialization.containsKey("meta")) {
            Object meta = serialization.get("meta");
            if (ItemMeta.class.isAssignableFrom(meta.getClass())) {
                function.setMeta((ItemMeta) meta);
            }
        }

        return function;
    }

}
