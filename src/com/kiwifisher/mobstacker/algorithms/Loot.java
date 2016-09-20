package com.kiwifisher.mobstacker.algorithms;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class Loot {

    private final Material material;
    private final short data;
    private final int minimumQuantity;
    private final int maxQuantity;
    private final double dropChance;
    private final boolean addLooting;

    // TODO: Convert loot types (RareLoot) etc. to Loot via additional options, create LootBuilder

    public Loot(Material material, int maxQuantity) {
        this(material, 0, maxQuantity);
    }

    public Loot(Material material, int maxQuantity, double dropChance) {
        this(material, (short) 0, 0, maxQuantity, dropChance, true);
    }

    public Loot(Material material, int minimumQuantity, int maxQuantity) {
        this(material, (short) 0, minimumQuantity, maxQuantity, true);
    }

    public Loot(Material material, int minimumQuantity, int maxQuantity, boolean addLooting) {
        this(material, (short) 0, minimumQuantity, maxQuantity, addLooting);
    }

    public Loot(Material material, short data, int minimumQuantity, int maxQuantity, boolean addLooting) {
        this(material, data, minimumQuantity, maxQuantity, 1, addLooting);
    }

    public Loot(Material material, short data, int maxQuantity, double dropChance, boolean addLooting) {
        this(material, data, 0, maxQuantity, dropChance, addLooting);
    }

    public Loot(Material material, int maxQuantity, double dropChance, boolean addLooting) {
        this(material, (short) 0, 0, maxQuantity, dropChance, addLooting);
    }

    private Loot(Material material, short data, int minimumQuantity, int maxQuantity, double dropChance, boolean addLooting) {
        this.material = material;
        this.data = data;
        this.minimumQuantity = minimumQuantity;
        this.maxQuantity = maxQuantity;
        this.dropChance = dropChance;
        this.addLooting = addLooting;
    }

    public Material getMaterial(Entity entity) {
        return this.material;
    }

    public short getData(Entity entity) {
        return this.data;
    }

    public int getMinimumQuantity() {
        return this.minimumQuantity;
    }

    public int getMaxQuantity() {
        return this.maxQuantity;
    }

    public double getDropChance(int looting) {
        return this.dropChance;
    }

    public boolean lootingAddsResults() {
        return this.addLooting;
    }

}
