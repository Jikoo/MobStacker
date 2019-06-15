package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

public class PlayerLeashEntityListener implements Listener {

    private final MobStacker plugin;

    public PlayerLeashEntityListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLeashEntity(PlayerLeashEntityEvent event) {
        // Ensure stacks cannot be leashed.
        if (!plugin.getConfig().getBoolean("leash-whole-stack") || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        // If the entity is a stack, peel off the rest.
        if (plugin.getStackUtils().getStackSize(entity) > 1) {
            plugin.getStackUtils().peelOffStack(entity);
        }

    }

    @EventHandler
    public void playerUnleashEvent(PlayerUnleashEntityEvent event) {
        // Attempt to re-stack unleashed entities.
        if (event.getEntity() instanceof LivingEntity) {
            plugin.getStackUtils().attemptToStack((LivingEntity) event.getEntity(), 1);
        }
    }

}
