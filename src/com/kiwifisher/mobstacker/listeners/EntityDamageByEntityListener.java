package com.kiwifisher.mobstacker.listeners;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for EntityDamageByEntityEvents. Used to manage swipe attacks affecting stacked entities.
 * 
 * @author Jikoo
 */
public class EntityDamageByEntityListener implements Listener {

    private final MobStacker plugin;
    /*
     * A little hacky, but there's no need to reinvent the wheel. Similar to
     * java.util.Collections#newSetFromMap, use a Cache to temporarily store UUIDs without having to
     * manually handle cleanup.
     */
    // TODO: Reset time on hotbar item change?
    private final Cache<UUID, Boolean> swipes = CacheBuilder.newBuilder()
            .expireAfterAccess(625, TimeUnit.MILLISECONDS).build();

    public EntityDamageByEntityListener(MobStacker plugin) {
        this.plugin = plugin;
    }

    /*
     * I don't like to suppress warnings, however, in this case it seems justified. This is not an
     * anticheat plugin - one assumes that if a player is reporting invalid states, either the
     * server administrator does not care to prevent it or the server's anticheat will prevent it.
     * There is no alternative aside from decently extensive and potentially inaccurate
     * velocity/block checking.
     */
    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Entity entity = event.getEntity();

        // Ensure whole stack damaging is enabled, the entity is a stack, and swipe damaging is enabled.
        if (!plugin.getConfig().getBoolean("persistent-damage.enable") || StackUtils.getStackSize(entity) < 2
                || !plugin.getConfig().getBoolean("persistent-damage.swipe-attack-whole-stack")
                || !(event.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

        // Swipe attacks require players to be on the ground and moving relatively slowly.
        if (player.isSprinting() || !player.isOnGround()) {
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();

        // Ensure item in hand is a sword.
        if (hand == null || !hand.getType().name().endsWith("_SWORD")) {
            return;
        }

        // Ensure the player is not on cooldown.
        if (this.swipes.getIfPresent(player.getUniqueId()) != null) {
            return;
        }

        this.swipes.put(player.getUniqueId(), true);
        // Swipe attack always does half a heart.
        plugin.getStackUtils().damageAverageHealth(event.getEntity(), 0.5);
    }

}
