package com.kiwifisher.mobstacker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.kiwifisher.mobstacker.commands.MobStackerCommands;
import com.kiwifisher.mobstacker.listeners.ChunkLoadListener;
import com.kiwifisher.mobstacker.listeners.ChunkUnloadListener;
import com.kiwifisher.mobstacker.listeners.CreatureSpawnListener;
import com.kiwifisher.mobstacker.listeners.EntityDamageListener;
import com.kiwifisher.mobstacker.listeners.EntityDeathListener;
import com.kiwifisher.mobstacker.listeners.EntityExplodeListener;
import com.kiwifisher.mobstacker.listeners.EntityTameListener;
import com.kiwifisher.mobstacker.listeners.ItemSpawnListener;
import com.kiwifisher.mobstacker.listeners.PlayerInteractEntityListener;
import com.kiwifisher.mobstacker.listeners.PlayerLeashEntityListener;
import com.kiwifisher.mobstacker.listeners.PlayerShearEntityListener;
import com.kiwifisher.mobstacker.listeners.SheepDyeListener;
import com.kiwifisher.mobstacker.listeners.SheepRegrowWoolListener;
import com.kiwifisher.mobstacker.loot.LootManager;
import com.kiwifisher.mobstacker.loot.impl.ConditionKilledByPlayer;
import com.kiwifisher.mobstacker.loot.impl.ConditionPropertiesAdult;
import com.kiwifisher.mobstacker.loot.impl.ConditionPropertiesOnFire;
import com.kiwifisher.mobstacker.loot.impl.ConditionSlimeSize;
import com.kiwifisher.mobstacker.loot.impl.ExperienceEntry;
import com.kiwifisher.mobstacker.loot.impl.ExperiencePool;
import com.kiwifisher.mobstacker.loot.impl.FunctionFurnaceSmelt;
import com.kiwifisher.mobstacker.loot.impl.FunctionLootingBonus;
import com.kiwifisher.mobstacker.loot.impl.FunctionMatchSheepWool;
import com.kiwifisher.mobstacker.loot.impl.FunctionSetData;
import com.kiwifisher.mobstacker.loot.impl.FunctionSetMeta;
import com.kiwifisher.mobstacker.loot.impl.LootEntry;
import com.kiwifisher.mobstacker.loot.impl.LootPool;
import com.kiwifisher.mobstacker.loot.impl.RandomChance;
import com.kiwifisher.mobstacker.loot.impl.SlimeExperienceEntry;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class MobStacker extends JavaPlugin {

    private StackUtils stackUtils;
    private LootManager lootManager;
    private final Map<String, Boolean> nerfSpawnerMobsWorlds = new HashMap<>();

    private boolean nerfSpawnerMobsDefault = false;
    private boolean stacking = true;

    @Override
    public void onLoad() {
        registerSerializableClasses();
    }

    static void registerSerializableClasses() {
        ConfigurationSerialization.registerClass(ConditionKilledByPlayer.class);
        ConfigurationSerialization.registerClass(ConditionPropertiesAdult.class);
        ConfigurationSerialization.registerClass(ConditionPropertiesOnFire.class);
        ConfigurationSerialization.registerClass(ConditionSlimeSize.class);
        ConfigurationSerialization.registerClass(ExperienceEntry.class);
        ConfigurationSerialization.registerClass(ExperiencePool.class);
        ConfigurationSerialization.registerClass(FunctionFurnaceSmelt.class);
        ConfigurationSerialization.registerClass(FunctionLootingBonus.class);
        ConfigurationSerialization.registerClass(FunctionMatchSheepWool.class);
        ConfigurationSerialization.registerClass(FunctionSetData.class);
        ConfigurationSerialization.registerClass(FunctionSetMeta.class);
        ConfigurationSerialization.registerClass(LootEntry.class);
        ConfigurationSerialization.registerClass(LootPool.class);
        ConfigurationSerialization.registerClass(RandomChance.class);
        ConfigurationSerialization.registerClass(SlimeExperienceEntry.class);
    }
 
    @Override
    public void onEnable() {
        // In case of the plugin being unloaded by another source, re-register classes on enable.
        registerSerializableClasses();

        saveDefaultConfig();

        this.stackUtils = new StackUtils(this);
        this.lootManager = new LootManager(this);

        this.getServer().getPluginManager().registerEvents(new ChunkLoadListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ChunkUnloadListener(this), this);
        this.getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityDamageListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityExplodeListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityTameListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ItemSpawnListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteractEntityListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLeashEntityListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerShearEntityListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SheepDyeListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SheepRegrowWoolListener(this), this);

        getCommand("mobstacker").setExecutor(new MobStackerCommands(this));

        File spigotyml = new File("spigot.yml");

        // Due to Spigot mods existing, just nerf spawner mobs based on spigot.yml's existence.
        if (spigotyml.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(spigotyml);
            ConfigurationSection worlds = config.getConfigurationSection("world-settings");

            if (worlds != null) {
                nerfSpawnerMobsDefault = worlds.getBoolean("default.nerf-spawner-mobs");

                for (String world : worlds.getKeys(false)) {
                    if (world.equals("default")) {
                        continue;
                    }
                    if (worlds.isBoolean(world + ".nerf-spawner-mobs")) {
                        nerfSpawnerMobsWorlds.put(world.toLowerCase(), worlds.getBoolean(world + ".nerf-spawner-mobs"));
                    }
                }
            }
        }

    }

    @Override
    public void onDisable() {}

    public void removeAllStacks() {

        for (World world : getServer().getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (StackUtils.getStackSize(entity) > 1) {
                    entity.remove();
                }
            }
        }

    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        if (this.stackUtils != null) {
            // Reload StackUtils
            this.stackUtils.loadConfig();
        }
    }

    public void log(String string) {
        this.getLogger().info(string);
    }

    public boolean isStacking() {
        return stacking;
    }

    public void setStacking(boolean bool) {
        stacking = bool;
    }

    public int getSearchTime() {
        return getConfig().getInt("seconds-to-try-stack");
    }

    public int getMaxStackSize(EntityType type) {
        return Math.max(1, getConfig().getInt("max-stack-sizes." + type.name(),
                getConfig().getInt("max-stack-sizes.default", Integer.MAX_VALUE)));
    }

    public StackUtils getStackUtils() {
        return stackUtils;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public boolean nerfSpawnerMobs(World world) {
        String name = world.getName().toLowerCase();
        if (nerfSpawnerMobsWorlds.containsKey(name)) {
            return nerfSpawnerMobsWorlds.get(name);
        }
        return nerfSpawnerMobsDefault;
    }
}
