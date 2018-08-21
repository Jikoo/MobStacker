package com.kiwifisher.mobstacker.listeners;

import java.util.List;

import com.kiwifisher.mobstacker.MobStacker;
import com.kiwifisher.mobstacker.utils.StackUtils;

import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

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

        // Ensure whole stack damaging is enabled and the entity is a stack.
        if (!plugin.getConfig().getBoolean("persistent-damage.enable") || StackUtils.getStackSize(entity) < 2) {
            return;
        }

        List<String> damageReasons = plugin.getConfig().getStringList("persistent-damage.reasons");

        // Ensure damage cause is configured to damage stack.
        if (damageReasons.contains(event.getCause().name())) {
            plugin.getStackUtils().damageAverageHealth(entity, event.getFinalDamage());
            return;
        }

        // Special case: A sweep attack returns DamageCause.ENTITY_ATTACK for the primary entity involved.
        if (event.getCause() != DamageCause.ENTITY_ATTACK
                || !(event instanceof EntityDamageByEntityEvent)
                || !damageReasons.contains("ENTITY_SWEEP_ATTACK")) {
            return;
        }

        Entity attackerEntity = ((EntityDamageByEntityEvent) event).getDamager();

        // Ensure attacker is at least a HumanEntity.
        if (!(attackerEntity instanceof HumanEntity)) {
            return;
        }

        HumanEntity attackerHuman = (HumanEntity) attackerEntity;

        ItemStack weapon = attackerHuman.getEquipment().getItemInMainHand();

        // Ensure weapon used is a sword that is off cooldown and attacker is eligible to sweep.
        if (weapon == null || attackerHuman.hasCooldown(weapon.getType())
                || !weapon.getType().name().endsWith("_SWORD") || !attackerHuman.isOnGround()
                || attackerHuman.isBlocking() || attackerHuman.isHandRaised()
                || attackerHuman instanceof Player && ((Player) attackerHuman).isSprinting()) {
            return;
        }

        // Just use doubles to prevent rounding issues, lots of casting looks messy.
        double sweepLevel = weapon.getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
        double damage = (attackerHuman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue()
                * sweepLevel) / (sweepLevel + 1D) + 1D;

        plugin.getStackUtils().damageAverageHealth(entity, damage);
    }

}
