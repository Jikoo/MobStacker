package com.kiwifisher.mobstacker;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kiwifisher.mobstacker.loot.api.ExperiencePool;
import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * Tests ensuring default configurations can be loaded properly.
 *
 * @author Jikoo
 */
public class JsonConfigTest {

    @Test
    public void testExperience() {
        Map<String, Map<String, ExperiencePool>> defaults = GenDefaultExperienceConfig.getConfigValues();
        Gson gson = MobStacker.getGson();
        try {
            Map<String, Map<String, ExperiencePool>> retrieved = gson.fromJson(gson.toJson(defaults),
                    new TypeToken<Map<String, Map<String, ExperiencePool>>>() {}.getType());
            compare(defaults, retrieved);
            Map<String, ExperiencePool> defaultWorld = retrieved.get("DEFAULT");
            if (defaultWorld == null) {
                fail("Unable to load defaults.");
            }
            EnumSet<EntityType> missing = EnumSet.noneOf(EntityType.class);
            for (EntityType type : EntityType.values()) {
                if (!type.isAlive() || type == EntityType.ARMOR_STAND || type == EntityType.PLAYER) {
                    continue;
                }
                if (!defaultWorld.containsKey(type.getKey().toString())) {
                    missing.add(type);
                }
            }
            if (!missing.isEmpty()) {
                fail("Missing EntityTypes: " + missing.toString());
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            fail("Encountered exception interpreting default config.");
        }
    }

    @SuppressWarnings("rawtypes")
    private static <T> void compare(Map<String, Map<String, T>> map1, Map<String, Map<String, T>> map2) {
        if (map1 == null || map2 == null) {
            fail("Compared maps cannot be null!");
        }

        if (map1.size() != map2.size()) {
            fail("Maps are not the same size");
        }

        for (Map.Entry<String, Map<String, T>> entry1 : map1.entrySet()) {
            if (!map2.containsKey(entry1.getKey())) {
                fail("Maps do not have identical keysets.");
            }
            Map<String, T> value2 = map2.get(entry1.getKey());

            for (Map.Entry<String, T> subentry1 : entry1.getValue().entrySet()) {
                if (!value2.containsKey(subentry1.getKey())) {
                    fail("Submappings do not have identical keysets.");
                }
                T subvalue1 = subentry1.getValue();
                T subvalue2 = value2.get(subentry1.getKey());
                if (subvalue1 instanceof Collection && subvalue2 instanceof Collection) {
                    if (!equal((Collection) subvalue1, (Collection) subvalue2)) {
                        fail("Unequal collection contents.");
                    }
                } else if (!subvalue1.equals(subvalue2)) {
                    fail("Unequal values.");
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static boolean equal(Collection collection1, Collection collection2) {
        if (collection1 == null && collection2 == null) {
            return true;
        }

        if (collection1 == null && collection2 != null || collection1 != null && collection2 == null
                || collection1.size() != collection2.size()) {
            return false;
        }

        if (collection1.isEmpty()) {
            // No need to check both, size already must be identical
            return true;
        }

        /*
         * Unfortunately, our elements are not capable of being sorted. We must iterate over the
         * entire contents to ensure each is contained. Since there's no guarantee that each
         * Collection contains only one of each object, we create a copy of one to modify.
         */
        List clonedElements = new ArrayList(collection2);

        for (Iterator iterator = collection1.iterator(); iterator.hasNext();) {
            if (!clonedElements.remove(iterator.next())) {
                return false;
            }
        }

        return clonedElements.isEmpty();
    }

}
