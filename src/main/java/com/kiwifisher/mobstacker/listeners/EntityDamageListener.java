package com.kiwifisher.mobstacker.listeners;

import com.kiwifisher.mobstacker.MobStacker;
import java.util.List;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
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

        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        List<String> damageReasons = plugin.getConfig().getStringList("restack-on-damage");

        if (damageReasons.contains(event.getCause().name())) {
            plugin.getStackUtils().attemptToStack(entity, 1);
        }

        // Ensure whole stack damaging is enabled and the entity is a stack.
        if (!plugin.getConfig().getBoolean("persistent-damage.enable")
                || plugin.getStackUtils().getStackSize(entity) < 2) {
            return;
        }

        damageReasons = plugin.getConfig().getStringList("persistent-damage.reasons");

        // Ensure damage cause is configured to damage stack.
        if (damageReasons.contains(event.getCause().name())) {
            plugin.getStackUtils().damageAverageHealth(entity, event.getFinalDamage());
            return;
        }

        if (!(event instanceof EntityDamageByEntityEvent)) {
            return;
        }

        Entity attackerEntity = ((EntityDamageByEntityEvent) event).getDamager();

        // Special case: piercing arrow
        if (attackerEntity instanceof AbstractArrow && damageReasons.contains("ARROW_PIERCE")) {
            AbstractArrow arrow = (AbstractArrow) attackerEntity;
            if (arrow.getPierceLevel() == 0) {
                return;
            }

            double averageDamage = event.getDamage() * arrow.getPierceLevel() / plugin.getStackUtils().getStackSize(entity);
            plugin.getStackUtils().damageAverageHealth(entity, averageDamage);
            arrow.setPierceLevel(0);
            return;
        }

        // Special case: A sweep attack returns DamageCause.ENTITY_ATTACK for the primary entity involved.
        if (event.getCause() != DamageCause.ENTITY_ATTACK
                || !damageReasons.contains("ENTITY_SWEEP_ATTACK")) {
            return;
        }

        // Ensure attacker is at least a HumanEntity.
        if (!(attackerEntity instanceof HumanEntity)) {
            return;
        }

        HumanEntity attackerHuman = (HumanEntity) attackerEntity;

        if (!attackerHuman.isOnGround() || attackerHuman.isBlocking() || attackerHuman.isHandRaised()
                || attackerHuman instanceof Player && ((Player) attackerHuman).isSprinting()
                || attackerHuman.getEquipment() == null) {
            return;
        }

        ItemStack weapon = attackerHuman.getEquipment().getItemInMainHand();

        // Ensure weapon used is a sword that is off cooldown and attacker is eligible to sweep.
        if (attackerHuman.hasCooldown(weapon.getType()) || !weapon.getType().name().endsWith("_SWORD")) {
            return;
        }

        AttributeInstance attackDamage = attackerHuman.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        // Just use doubles to prevent rounding issues, lots of casting looks messy.
        double sweepLevel = weapon.getEnchantmentLevel(Enchantment.SWEEPING_EDGE);
        double damage = sweepLevel < 1
                ? 1 : 1 + (attackDamage != null ? attackDamage.getValue() : 0) * (sweepLevel / (sweepLevel + 1));

        plugin.getStackUtils().damageAverageHealth(entity, damage);
    }

}
