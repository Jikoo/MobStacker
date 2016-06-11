package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class EntityTameListener implements Listener {

    private final MobStacker plugin;

    public EntityTameListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTame(EntityTameEvent event) {

        LivingEntity entity = event.getEntity();

        // Ensure we have a stack.
        if (StackUtils.getStackSize(entity) < 2) {
            return;
        }

        // Peel off the remainder of the stack.
        plugin.getStackUtils().peelOffStack(entity);

    }

}
