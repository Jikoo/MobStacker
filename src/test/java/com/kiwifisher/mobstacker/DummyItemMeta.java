package com.kiwifisher.mobstacker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Dummy ItemMeta for serialization tests.
 * 
 * @author Jikoo
 */
@SerializableAs("ItemMeta")
public class DummyItemMeta implements ItemMeta {

    private String metaType;
    private String potionType;

    public DummyItemMeta() {}

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialization = new HashMap<>();

        if (metaType != null) {
            serialization.put("meta-type", metaType);
        }

        if (potionType != null) {
            serialization.put("potion-type", potionType);
        }

        return serialization;
    }

    public static DummyItemMeta deserialize(Map<String, Object> serialization) {
        DummyItemMeta dummyMeta = new DummyItemMeta();

        if (serialization.containsKey("meta-type")) {
            Object value = serialization.get("meta-type");
            if (String.class.isInstance(value)) {
                dummyMeta.metaType = (String) value;
            }
        }

        if (serialization.containsKey("potion-type")) {
            Object value = serialization.get("potion-type");
            if (String.class.isInstance(value)) {
                dummyMeta.potionType = (String) value;
            }
        }

        return dummyMeta;
    }

    @Override
    public boolean addEnchant(Enchantment paramEnchantment, int paramInt, boolean paramBoolean) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public void addItemFlags(ItemFlag... paramArrayOfItemFlag) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public ItemMeta clone() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public String getDisplayName() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public int getEnchantLevel(Enchantment paramEnchantment) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public Map<Enchantment, Integer> getEnchants() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public String getLocalizedName() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public List<String> getLore() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean hasConflictingEnchant(Enchantment paramEnchantment) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean hasDisplayName() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean hasEnchant(Enchantment paramEnchantment) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean hasEnchants() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean hasItemFlag(ItemFlag paramItemFlag) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean hasLocalizedName() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean hasLore() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean isUnbreakable() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public boolean removeEnchant(Enchantment paramEnchantment) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public void removeItemFlags(ItemFlag... paramArrayOfItemFlag) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public void setDisplayName(String paramString) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public void setLocalizedName(String paramString) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public void setLore(List<String> paramList) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public void setUnbreakable(boolean paramBoolean) {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

    @Override
    public Spigot spigot() {
        throw new NotImplementedException("DummyItemMeta is for testing purposes only!");
    }

}
