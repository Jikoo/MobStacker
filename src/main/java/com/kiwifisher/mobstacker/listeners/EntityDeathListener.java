package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.loot.LootManager;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener implements Listener {

    private final MobStacker plugin;

    public EntityDeathListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        LivingEntity entity = event.getEntity();

        DamageCause lastDamage = entity.getLastDamageCause() != null ? entity.getLastDamageCause().getCause() : null;
        /*
         * If void is the cause of death, always let the entity just die. This prevents falling
         * stacks reaching excessive (and potentially problematic - see SPIGOT-58) depths.
         */
        if (lastDamage == DamageCause.VOID) {
            return;
        }

        int stackSize = plugin.getStackUtils().getStackSize(entity);

        // Ensure we have a stack.
        if (stackSize < 2) {
            return;
        }

        // Check if the whole stack is supposed to die.
        if (!plugin.getConfig().getBoolean("persistent-damage.enable")
                || plugin.getStackUtils().getAverageHealth(entity, false) > 0) {

            // Whole stack is not dying, peel off the remainder and return.
            plugin.getStackUtils().peelOffStack(entity);
            return;
        }

        // Check if we are dropping proportionate loot.
        if (!plugin.getConfig().getBoolean("persistent-damage.loot.enable") || lastDamage == null
                || plugin.getConfig().getStringList("persistent-damage.loot.ignore-reasons").contains(lastDamage.name())) {
            return;
        }

        int entitiesLooted = Math.min(stackSize - 1, plugin.getConfig().getInt("persistent-damage.loot.max-multiplier"));

        if (entity instanceof Slime) {
            Slime splitting = (Slime) entity;
            if (splitting.getSize() > 1) {
                splitting.getWorld().spawn(splitting.getLocation(), splitting.getClass(), slime -> {
                    slime.setSize(splitting.getSize() / 2);
                    plugin.getStackUtils().setStackSize(slime, ThreadLocalRandom.current().nextInt(entitiesLooted * 2, entitiesLooted * 4));
                });
            }
        }

        LootManager manager = plugin.getLootManager();

        // Set proportionate experience.
        if (event.getEntity().getKiller() != null) {
            event.setDroppedExp(event.getDroppedExp() + manager.getExperience(entity, entitiesLooted));
        }

        // Try to drop proportionate loot.
        event.getDrops().addAll(manager.getLoot(entity, entitiesLooted));

    }

}
