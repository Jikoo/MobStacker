package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractEntityListener implements Listener {

    private final MobStacker plugin;

    public PlayerInteractEntityListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();

        // Ensure item in hand is a name tag with meta.
        if (hand.getType() != Material.NAME_TAG || !hand.hasItemMeta()) {
            return;
        }

        ItemMeta meta = hand.getItemMeta();

        // Ensure tag has a name set.
        if (!meta.hasDisplayName()) {
            return;
        }

        // Prevent renaming using tags matching the naming pattern if stacks will load.
        if (plugin.getConfig().getBoolean("load-existing-stacks.enabled")
                && plugin.getStackUtils().matchesStackName(meta.getDisplayName())) {
            event.setCancelled(true);
            return;
        }
 
        // Only handle if custom named mobs do not stack.
        if (plugin.getConfig().getBoolean("stack-custom-named-mobs")) {
            return;
        }

        Entity renamed = event.getRightClicked();

        // Ensure the entity is a stack.
        if (StackUtils.getStackSize(renamed) < 2) {
            return;
        }

        // Peel off the rest of the stack.
        plugin.getStackUtils().peelOffStack(renamed);

    }

}
