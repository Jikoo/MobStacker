package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for ItemSpawnEvents.
 * 
 * @author Jikoo
 */
public class ItemSpawnListener implements Listener {

    private final MobStacker plugin;

    public ItemSpawnListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (!plugin.getConfig().getBoolean("multiply-eggs.enabled")) {
            return;
        }

        ItemStack stack = event.getEntity().getItemStack();

        if (stack.getType() != Material.EGG || stack.getAmount() > 1) {
            return;
        }

        for (Entity entity : event.getEntity().getNearbyEntities(0.00001, 0.00001, 0.00001)) {
            if (!(entity instanceof Chicken)) {
                continue;
            }
            stack.setAmount(Math.max(1, Math.min(plugin.getConfig().getInt("multiply-eggs.max"), plugin.getStackUtils().getStackSize((LivingEntity) entity))));
            event.getEntity().setItemStack(stack);
            return;
        }
    }

}
