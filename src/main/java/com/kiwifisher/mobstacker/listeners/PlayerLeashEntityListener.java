package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.entity.Entity;
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

        Entity entity = event.getEntity();

        // If the entity is a stack and leashing whole stacks is not permitted, peel off the rest.
        if (StackUtils.getStackSize(entity) > 1
                && !plugin.getConfig().getBoolean("leash-whole-stack")) {
            plugin.getStackUtils().peelOffStack(entity);
        }

    }

    @EventHandler
    public void playerUnleashEvent(PlayerUnleashEntityEvent event) {

        Entity entity = event.getEntity();

        plugin.getStackUtils().attemptToStack(entity, 1);

    }

}
