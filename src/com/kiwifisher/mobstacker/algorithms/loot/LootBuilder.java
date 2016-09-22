package com.kiwifisher.mobstacker.algorithms.loot;

import com.google.common.base.Preconditions;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

/**
 * Builder for Loot.
 * 
 * @author Jikoo
 */
public class LootBuilder {

    private Function<Entity, Material> materialFunction;
    private Function<Entity, Short> dataFunction;
    private Integer minimumQuantity, maximumQuantity;
    private Double dropChance, lootingModifier;
    private Boolean lootingAddsResults, playerKillRequired;

    /**
     * Constructor for a LootBuilder with the drop Material provided.
     * 
     * @param material the Material to drop
     */
    public LootBuilder(Material material) {
        this.materialFunction = new Function<Entity, Material>() {
            @Override
            public Material apply(Entity t) {
                return material;
            }
        };
    }

    /**
     * Constructor for a LootBuilder with the drop Material Function as a parameter.
     * 
     * @param function the Function for providing a Material
     */
    public LootBuilder(Function<Entity, Material> function) {

        this.materialFunction = function;
    }

    /**
     * Sets the Loot's drop data to the value provided.
     * 
     * @throws IllegalArgumentException if the data Function is already set
     * @param data the data
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withData(short data) {
        Preconditions.checkArgument(this.dataFunction == null, "Data function already set!");

        this.dataFunction = new Function<Entity, Short>() {
            @Override
            public Short apply(Entity t) {
                return data;
            }
        };
        return this;
    }

    /**
     * Sets the Loot's drop data Function to the Function provided.
     * 
     * @throws IllegalArgumentException if the data Function is already set
     * @param function the Function
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withDataFunction(Function<Entity, Short> function) {
        Preconditions.checkArgument(this.dataFunction == null, "Data function already set!");

        this.dataFunction = function;
        return this;
    }

    /**
     * Sets the Loot's minimum quantity to the value provided.
     * 
     * @throws IllegalArgumentException if the minimum quantity is already set
     * @param minimum the minimum quantity
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withMinimum(int minimum) {
        Preconditions.checkArgument(this.minimumQuantity == null, "Minimum already set!");

        this.minimumQuantity = minimum;
        return this;
    }

    /**
     * Sets the Loot's maximum quantity to the value provided.
     * 
     * @throws IllegalArgumentException if the maximum quantity is already set
     * @param maximum the maximum quantity
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withMaximum(int maximum) {
        Preconditions.checkArgument(this.maximumQuantity == null, "Maximum already set!");
        Preconditions.checkArgument(maximum > 0, "Maximum cannot be less than 1!");

        this.maximumQuantity = maximum;
        return this;
    }

    /**
     * Sets the Loot's drop chance to the value provided. Drop chance is a value between 0 and 1
     * where 0 results in no drops and 1 results in guaranteed drops.
     * 
     * @throws IllegalArgumentException if the drop chance is already set or the value is not between 0 and 1 exclusive
     * @param chance the drop chance
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withDropChance(double chance) {
        Preconditions.checkArgument(this.dropChance == null, "Drop chance already set!");
        Preconditions.checkArgument(chance > 0, "Drop chance must be greater than 0.");
        // There's no point in a drop chance over 1, the system doesn't support it. Set a higher min and max.
        Preconditions.checkArgument(chance < 1, "Drop chance must be less than 1. Set a higher minimum instead!");

        this.dropChance = chance;
        return this;
    }

    /**
     * Sets the Loot's additional looting drop chance per level to the value provided.
     * 
     * @throws IllegalArgumentException if the additional looting modifier is already set
     * @param modifier the additional looting modifier
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withLootingDropChanceModifier(double modifier) {
        Preconditions.checkArgument(this.lootingModifier == null, "Looting modifier already set!");

        this.lootingModifier = modifier;
        return this;
    }

    /**
     * Sets the Loot to add the looting level used to the maximum number of drops.
     * 
     * @throws IllegalArgumentException if lootingAddsResults is already set
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withAdditionalLootingResults() {
        Preconditions.checkArgument(this.lootingAddsResults == null, "Looting adding additional results already set!");

        this.lootingAddsResults = true;
        return this;
    }

    /**
     * Sets the Loot to require a Player to be the killer.
     * 
     * @throws IllegalArgumentException if playerKillRequired is already set
     * @return the LootBuilder for call chaining
     */
    public LootBuilder withPlayerKillRequired() {
        Preconditions.checkArgument(this.playerKillRequired == null, "Player kill already set!");

        this.playerKillRequired = true;
        return this;
    }

    /**
     * Constructs a Loot from the given LootBuilder. All values not explicitly set will assume their
     * defaults.
     * <p>
     * Defaults:
     * <pre>
     * Minimum quantity = 0
     * Maximum quantity = 1
     * Drop chance = 1 (guaranteed roll between min and max inclusive)
     * Looting modifier = 0
     * Additional looting results = false
     * Player kill required = false
     * </pre>
     * 
     * @return the Loot
     */
    public Loot toLoot() {
        if (dataFunction == null) {
            dataFunction = new Function<Entity, Short>() {
                @Override
                public Short apply(Entity t) {
                    return 0;
                }
            };
        }

        if (minimumQuantity == null) {
            minimumQuantity = 0;
        }

        if (maximumQuantity == null) {
            maximumQuantity = 1;
        }

        if (dropChance != null) {
            dropChance = 1D;
        }

        if (lootingModifier == null) {
            lootingModifier = 0D;
        }

        if (lootingAddsResults == null) {
            lootingAddsResults = false;
        }

        if (playerKillRequired == null) {
            playerKillRequired = false;
        }

        return new Loot(materialFunction, dataFunction, minimumQuantity, maximumQuantity, dropChance, lootingModifier, lootingAddsResults, playerKillRequired);
    }

}
