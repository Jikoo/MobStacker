package com.kiwifisher.mobstacker.algorithms;

import com.kiwifisher.mobstacker.algorithms.creatures.AnimalLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.BlazeLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.ChickenLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.CowLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.CreeperLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.EndermanLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.ExperienceLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.GhastLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.GuardianLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.HorseLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.IronGolemLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.MagmaCubeLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.PigLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.PigZombieLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.PolarBearLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.RabbitLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.SheepLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.SkeletonLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.SlimeLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.SnowGolemLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.SpiderLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.SquidLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.WitchLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.WitherLootAlgorithm;
import com.kiwifisher.mobstacker.algorithms.creatures.ZombieLootAlgorithm;

public enum AlgorithmEnum {

    BAT(EmptyAlgorithm.getInstance()),
    BLAZE(new BlazeLootAlgorithm()),
    CAVE_SPIDER(new SpiderLootAlgorithm()),
    CHICKEN(new ChickenLootAlgorithm()),
    COW(new CowLootAlgorithm()),
    CREEPER(new CreeperLootAlgorithm()),
    ENDERMAN(new EndermanLootAlgorithm()),
    ENDERMITE(new ExperienceLootAlgorithm(3, 3)),
    GHAST(new GhastLootAlgorithm()),
    // Giants always drop 5 experience.
    GIANT(new ExperienceLootAlgorithm(5, 5)),
    GUARDIAN (new GuardianLootAlgorithm()),
    HORSE(new HorseLootAlgorithm()),
    IRON_GOLEM(new IronGolemLootAlgorithm()),
    MAGMA_CUBE(new MagmaCubeLootAlgorithm()),
    // Cows and mooshrooms have the same drops.
    MUSHROOM_COW(COW.lootAlgorithm),
    // Ocelots have no drops, the animal algorithm base works.
    OCELOT(new AnimalLootAlgorithm()),
    PIG (new PigLootAlgorithm()),
    PIG_ZOMBIE(new PigZombieLootAlgorithm()),
    POLAR_BEAR(new PolarBearLootAlgorithm()),
    RABBIT(new RabbitLootAlgorithm()),
    SHEEP(new SheepLootAlgorithm()),
    // Silverfish and shulkers always drop 5 experience.
    SILVERFISH(GIANT.lootAlgorithm),
    SHULKER(GIANT.lootAlgorithm),
    SKELETON(new SkeletonLootAlgorithm()),
    SLIME(new SlimeLootAlgorithm()),
    SNOW_GOLEM(new SnowGolemLootAlgorithm()),
    // Spiders and cave spiders have the same drops.
    SPIDER(CAVE_SPIDER.lootAlgorithm),
    SQUID(new SquidLootAlgorithm()),
    VILLAGER(EmptyAlgorithm.getInstance()),
    WITCH(new WitchLootAlgorithm()),
    WITHER(new WitherLootAlgorithm()),
    // Wolves have no drops, the animal algorithm base works.
    WOLF(OCELOT.lootAlgorithm),
    ZOMBIE(new ZombieLootAlgorithm());

    private final LootAlgorithm lootAlgorithm;

    AlgorithmEnum(LootAlgorithm lootAlgorithm) {
        this.lootAlgorithm = lootAlgorithm;
    }

    public LootAlgorithm getLootAlgorithm() {
        return this.lootAlgorithm;
    }

}
