package com.kiwifisher.mobstacker;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Map;

import com.github.jikoo.GenDefaultExperienceConfig;
import com.github.jikoo.GenDefaultLootConfig;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import com.kiwifisher.mobstacker.loot.api.IExperiencePool;
import com.kiwifisher.mobstacker.loot.api.ILootPool;

import org.junit.Test;

/**
 * Tests ensuring default configurations can be loaded properly.
 * 
 * @author Jikoo
 */
public class JsonConfigTest {

    @Test
    public void testLoot() {
        String output = GenDefaultLootConfig.getConfigJSON();
        try {
            MobStacker.getGson().fromJson(output,
                    new TypeToken<Map<String, Map<String, Collection<ILootPool>>>>() {}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            fail("Encountered exception interpreting default config.");
        }
    }

    @Test
    public void testExperience() {
        String output = GenDefaultExperienceConfig.getConfigJSON();
        try {
            MobStacker.getGson().fromJson(output,
                    new TypeToken<Map<String, Map<String, IExperiencePool>>>() {}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            fail("Encountered exception interpreting default config.");
        }
    }

}
