package com.kiwifisher.mobstacker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kiwifisher.mobstacker.loot.api.IExperienceEntry;
import com.kiwifisher.mobstacker.loot.api.IExperiencePool;
import com.kiwifisher.mobstacker.loot.impl.ConditionPropertiesAdult;
import com.kiwifisher.mobstacker.loot.impl.ExperienceEntry;
import com.kiwifisher.mobstacker.loot.impl.ExperiencePool;
import com.kiwifisher.mobstacker.loot.impl.SlimeExperienceEntry;

/**
 * Generates the default experience.yml file.
 * 
 * @author Jikoo
 */
public class GenDefaultExperienceConfig {

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("experience.json")))) {
            writer.write(MobStacker.getGson().toJson(getConfigValues()));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Map<String, IExperiencePool>> getConfigValues() {
        Map<String, IExperiencePool> defaults = new HashMap<>();

        ExperiencePool pool;
        List<IExperienceEntry> entries;
        ExperienceEntry entry;

        pool = new ExperiencePool();
        defaults.put("BAT", pool);
        defaults.put("IRON_GOLEM", pool);
        defaults.put("SNOW_GOLEM", pool);
        defaults.put("VILLAGER", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(10);
        pool.setEntries(Arrays.asList(entry));
        defaults.put("BLAZE", pool);
        defaults.put("GUARDIAN", pool);
        defaults.put("ELDER_GUARDIAN", pool);
        defaults.put("EVOCATION_ILLAGER", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(1);
        entry.setMaximum(3);
        entry.setConditions(Arrays.asList(new ConditionPropertiesAdult()));
        pool.setEntries(Arrays.asList(entry));
        defaults.put("CHICKEN", pool);
        defaults.put("COW", pool);
        defaults.put("DONKEY", pool);
        defaults.put("HORSE", pool);
        defaults.put("LLAMA", pool);
        defaults.put("MULE", pool);
        defaults.put("MUSHROOM_COW", pool);
        defaults.put("OCELOT", pool);
        defaults.put("PIG", pool);
        defaults.put("POLAR_BEAR", pool);
        defaults.put("SHEEP", pool);
        defaults.put("SKELETON_HORSE", pool);
        defaults.put("SQUID", pool);
        defaults.put("RABBIT", pool);
        defaults.put("WOLF", pool);
        defaults.put("ZOMBIE_HORSE", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(5);
        pool.setEntries(Arrays.asList(entry));
        defaults.put("CAVE_SPIDER", pool);
        defaults.put("CREEPER", pool);
        defaults.put("ENDERMAN", pool);
        defaults.put("GHAST", pool);
        defaults.put("SHULKER", pool);
        defaults.put("SILVERFISH", pool);
        defaults.put("SKELETON", pool);
        defaults.put("SPIDER", pool);
        defaults.put("STRAY", pool);
        defaults.put("VINDICATION_ILLAGER", pool);
        defaults.put("WITCH", pool);
        defaults.put("WITHER_SKELETON", pool);

        pool = new ExperiencePool();
        entries = new ArrayList<>();
        entry = new ExperienceEntry();
        entry.setMinimum(5);
        entries.add(entry);
        entry = new ExperienceEntry();
        // Baby zombies drop 12 exp, base guarantees 5. 7 needs to be added if not adult.
        entry.setMinimum(7);
        ConditionPropertiesAdult conditionAdult = new ConditionPropertiesAdult();
        conditionAdult.setAdult(false);
        entry.setConditions(Arrays.asList(conditionAdult));
        entries.add(entry);
        pool.setEntries(entries);
        defaults.put("HUSK", pool);
        defaults.put("PIG_ZOMBIE", pool);
        defaults.put("ZOMBIE", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(3);
        pool.setEntries(Arrays.asList(entry));
        defaults.put("ENDERMITE", pool);
        defaults.put("VEX", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(500);
        pool.setEntries(Arrays.asList(entry));
        defaults.put("ENDER_DRAGON", pool);

        pool = new ExperiencePool();
        entry = new ExperienceEntry();
        entry.setMinimum(50);
        pool.setEntries(Arrays.asList(entry));
        defaults.put("WITHER", pool);

        pool = new ExperiencePool();
        pool.setEntries(Arrays.asList(new SlimeExperienceEntry()));
        defaults.put("SLIME", pool);
        defaults.put("MAGMA_CUBE", pool);

        Map<String, Map<String, IExperiencePool>> mappings = new HashMap<>();
        mappings.put("DEFAULT", defaults);

        return mappings;

    }

}
