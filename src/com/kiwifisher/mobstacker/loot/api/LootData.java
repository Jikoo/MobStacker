package com.kiwifisher.mobstacker.loot.api;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Container for several values to be altered by IFunctions.
 * 
 * @author Jikoo
 */
public class LootData {

    private Material material;
    private short data;
    private ItemMeta meta;
    private int minimumQuantity, maximumQuantity;

    public LootData(Material material, int minimumQuantity, int maximumQuantity) {
        this.material = material;
        this.data = 0;
        this.minimumQuantity = minimumQuantity;
        this.maximumQuantity = maximumQuantity;
    }

    /**
     * @return the material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * @param material the material to set
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * @return the data value
     */
    public short getData() {
        return data;
    }

    /**
     * @param data the data value to set
     */
    public void setData(short data) {
        this.data = data;
    }

    /**
     * @return the meta
     */
    public ItemMeta getMeta() {
        return meta;
    }

    /**
     * @param meta the meta to set
     */
    public void setMeta(ItemMeta meta) {
        this.meta = meta;
    }

    /**
     * @return the minimum quantity
     */
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    /**
     * @param minimumQuantity the minimum quantity to set
     */
    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    /**
     * @return the maximum quantity
     */
    public int getMaximumQuantity() {
        return maximumQuantity;
    }

    /**
     * @param maximumQuantity the maximum quantity to set
     */
    public void setMaximumQuantity(int maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

}
