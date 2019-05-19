package com.kiwifisher.mobstacker.utils;

import com.kiwifisher.mobstacker.loot.api.Condition;
import com.kiwifisher.mobstacker.loot.api.Conditional;
import org.bukkit.entity.Entity;

import java.util.Collection;

/**
 * Utility for testing IConditions.
 *
 * @author Jikoo
 */
public class ConditionUtils {

    private ConditionUtils() {}

    public static boolean meetsConditions(Entity entity, Conditional conditional) {
        Collection<Condition> conditions = conditional.getConditions();

        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        for (Condition condition : conditions) {
            if (!condition.test(entity)) {
                return false;
            }
        }

        return true;
    }

}
