package com.kiwifisher.mobstacker.loot.impl;

import java.util.Map;

import com.google.gson.annotations.Expose;

import com.kiwifisher.mobstacker.loot.api.LootData;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * IFunction implementation for setting the resulting stack's ItemMeta.
 * 
 * @author Jikoo
 */
public class FunctionSetMeta extends Function {

    @Expose
    private Map<String, Object> serializedMeta;
    private ItemMeta meta;

    public FunctionSetMeta() {}

    public ItemMeta getMeta() {
        if (meta == null && serializedMeta != null) {
            meta = (ItemMeta) ConfigurationSerialization.deserializeObject(serializedMeta);
        }
        return meta;
    }

    public void setMeta(ItemMeta meta) {
        this.meta = meta;
        if (meta != null) {
            this.serializedMeta = meta.serialize();
            /*
             * Ensure deserialization is possible. Bukkit's serialization system only includes this
             * required key when saving to YAML, as it's a magic value added later. Users are not
             * expected to know it's there and know to include it.
             */
            this.serializedMeta.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, "ItemMeta");
        } else {
            this.serializedMeta = null;
        }
    }

    public void setSerializedMeta(Map<String, Object> serializedMeta) {
        // Null meta so it is deserialized later on demand. Allows for testing without OBC/NMS.
        this.meta = null;
        this.serializedMeta = serializedMeta;
    }

    @Override
    public void modify(LootData lootData, Entity entity, int looting) {
        lootData.setMeta(getMeta());
    }

    @Override
    public boolean isVariable() {
        return false;
    }

}
