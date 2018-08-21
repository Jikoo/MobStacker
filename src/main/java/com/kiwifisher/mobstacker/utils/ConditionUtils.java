package com.kiwifisher.mobstacker.utils;

import java.util.Collection;

import com.kiwifisher.mobstacker.loot.api.ICondition;
import com.kiwifisher.mobstacker.loot.api.IConditional;

import org.bukkit.entity.Entity;

/**
 * Utility for testing IConditions.
 *
 * @author Jikoo
 */
public class ConditionUtils {

    private ConditionUtils() {}

    public static boolean meetsConditions(Entity entity, IConditional conditional) {
        Collection<ICondition> conditions = conditional.getConditions();

        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        for (ICondition condition : conditions) {
            if (!condition.test(entity)) {
                return false;
            }
        }

        return true;
    }

}
