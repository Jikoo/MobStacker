package com.kiwifisher.mobstacker;

import com.kiwifisher.mobstacker.loot.api.ExperienceEntry;
import com.kiwifisher.mobstacker.loot.api.ExperiencePool;
import com.kiwifisher.mobstacker.loot.impl.ConditionPropertiesAdult;
import com.kiwifisher.mobstacker.loot.impl.BaseExperienceEntry;
import com.kiwifisher.mobstacker.loot.impl.BaseExperiencePool;
import com.kiwifisher.mobstacker.loot.impl.SlimeExperienceEntry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    static Map<String, Map<String, ExperiencePool>> getConfigValues() {
        Map<String, ExperiencePool> defaults = new HashMap<>();

        BaseExperiencePool pool;
        List<ExperienceEntry> entries;
        BaseExperienceEntry entry;

        pool = new BaseExperiencePool();
        defaults.put("minecraft:bat", pool);
        defaults.put("minecraft:dolphin", pool);
        defaults.put("minecraft:iron_golem", pool);
        defaults.put("minecraft:ravager", pool); // TODO double check, wiki shows no exp drop
        defaults.put("minecraft:snow_golem", pool);
        defaults.put("minecraft:villager", pool);;
        defaults.put("minecraft:wandering_trader", pool);

        pool = new BaseExperiencePool();
        entry = new BaseExperienceEntry();
        entry.setMinimum(10);
        pool.setEntries(Collections.singletonList(entry));
        defaults.put("minecraft:blaze", pool);
        defaults.put("minecraft:guardian", pool);
        defaults.put("minecraft:elder_guardian", pool);
        defaults.put("minecraft:evoker", pool);

        pool = new BaseExperiencePool();
        entry = new BaseExperienceEntry();
        entry.setMinimum(1);
        entry.setMaximum(3);
        entry.setConditions(Collections.singletonList(new ConditionPropertiesAdult()));
        pool.setEntries(Collections.singletonList(entry));
        defaults.put("minecraft:cat", pool);
        defaults.put("minecraft:chicken", pool);
        defaults.put("minecraft:cod", pool);
        defaults.put("minecraft:cow", pool);
        defaults.put("minecraft:donkey", pool);
        defaults.put("minecraft:horse", pool);
        defaults.put("minecraft:llama", pool);
        defaults.put("minecraft:mule", pool);
        defaults.put("minecraft:mooshroom", pool);
        defaults.put("minecraft:ocelot", pool);
        defaults.put("minecraft:parrot", pool);
        defaults.put("minecraft:pig", pool);
        defaults.put("minecraft:panda", pool);
        defaults.put("minecraft:polar_bear", pool);
        defaults.put("minecraft:pufferfish", pool);
        defaults.put("minecraft:salmon", pool);
        defaults.put("minecraft:sheep", pool);
        defaults.put("minecraft:skeleton_horse", pool);
        defaults.put("minecraft:squid", pool);
        defaults.put("minecraft:trader_llama", pool);
        defaults.put("minecraft:tropical_fish", pool);
        defaults.put("minecraft:turtle", pool);
        defaults.put("minecraft:rabbit", pool);
        defaults.put("minecraft:wolf", pool);
        defaults.put("minecraft:zombie_horse", pool);

        pool = new BaseExperiencePool();
        entry = new BaseExperienceEntry();
        entry.setMinimum(5);
        pool.setEntries(Collections.singletonList(entry));
        defaults.put("minecraft:cave_spider", pool);
        defaults.put("minecraft:creeper", pool);
        defaults.put("minecraft:enderman", pool);
        defaults.put("minecraft:ghast", pool);
        defaults.put("minecraft:giant", pool);
        defaults.put("minecraft:illusioner", pool);
        defaults.put("minecraft:phantom", pool);
        defaults.put("minecraft:pillager", pool);
        defaults.put("minecraft:shulker", pool);
        defaults.put("minecraft:silverfish", pool);
        defaults.put("minecraft:skeleton", pool);
        defaults.put("minecraft:spider", pool);
        defaults.put("minecraft:stray", pool);
        defaults.put("minecraft:vindicator", pool);
        defaults.put("minecraft:witch", pool);
        defaults.put("minecraft:wither_skeleton", pool);

        pool = new BaseExperiencePool();
        entries = new ArrayList<>();
        entry = new BaseExperienceEntry();
        entry.setMinimum(5);
        entries.add(entry);
        entry = new BaseExperienceEntry();
        // Baby zombies drop 12 exp, base guarantees 5. 7 needs to be added if not adult.
        entry.setMinimum(7);
        ConditionPropertiesAdult conditionAdult = new ConditionPropertiesAdult();
        conditionAdult.setAdult(false);
        entry.setConditions(Collections.singletonList(conditionAdult));
        entries.add(entry);
        pool.setEntries(entries);
        defaults.put("minecraft:drowned", pool);
        defaults.put("minecraft:husk", pool);
        defaults.put("minecraft:zombie", pool);
        defaults.put("minecraft:zombie_pigman", pool);
        defaults.put("minecraft:zombie_villager", pool);

        pool = new BaseExperiencePool();
        entry = new BaseExperienceEntry();
        entry.setMinimum(3);
        pool.setEntries(Collections.singletonList(entry));
        defaults.put("minecraft:endermite", pool);
        defaults.put("minecraft:vex", pool);

        pool = new BaseExperiencePool();
        entry = new BaseExperienceEntry();
        entry.setMinimum(500);
        pool.setEntries(Collections.singletonList(entry));
        defaults.put("minecraft:ender_dragon", pool);

        pool = new BaseExperiencePool();
        entry = new BaseExperienceEntry();
        entry.setMinimum(1);
        entry.setMaximum(2);
        entry.setConditions(Collections.singletonList(new ConditionPropertiesAdult()));
        pool.setEntries(Collections.singletonList(entry));
        defaults.put("minecraft:fox", pool);// TODO confirm 1-2 not typo of 1-3 like all other animals

        pool = new BaseExperiencePool();
        entry = new BaseExperienceEntry();
        entry.setMinimum(50);
        pool.setEntries(Collections.singletonList(entry));
        defaults.put("minecraft:wither", pool);

        pool = new BaseExperiencePool();
        pool.setEntries(Collections.singletonList(new SlimeExperienceEntry()));
        defaults.put("minecraft:slime", pool);
        defaults.put("minecraft:magma_cube", pool);

        Map<String, Map<String, ExperiencePool>> mappings = new HashMap<>();
        mappings.put("DEFAULT", defaults);

        return mappings;

    }

}
