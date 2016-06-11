package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Listener for EntityDamageEvents. Used to cause certain types of damage to persist into new stacks.
 * 
 * @author Jikoo
 */
public class EntityDamageListener implements Listener {

    private final MobStacker plugin;

    public EntityDamageListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {

        Entity entity = event.getEntity();

        // Ensure whole stack damaging is enabled, the entity is a stack, and the damage type counts.
        if (!plugin.getConfig().getBoolean("persistent-damage.enable") || StackUtils.getStackSize(entity) < 2
                || !plugin.getConfig().getStringList("persistent-damage.reasons").contains(event.getCause().name())) {
            return;
        }

        plugin.getStackUtils().damageAverageHealth(entity, event.getFinalDamage());
    }

}
