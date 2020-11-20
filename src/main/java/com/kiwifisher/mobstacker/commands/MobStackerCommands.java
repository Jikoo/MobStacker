package com.kiwifisher.mobstacker.commands;

import com.kiwifisher.mobstacker.MobStacker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobStackerCommands implements CommandExecutor {

    private final MobStacker plugin;

    public MobStackerCommands(MobStacker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            args[0] = args[0].toLowerCase();
        }

        if (args.length < 1 || args[0].equals("help")) {
            if (sender.hasPermission("mobstacker.toggle")) {
                sender.sendMessage(ChatColor.YELLOW + "/mobstacker toggle" + ChatColor.GRAY + " - Toggle whether mobs stack globally.");
            }
            if (sender.hasPermission("mobstacker.reload")) {
                sender.sendMessage(ChatColor.YELLOW + "/mobstacker reload" + ChatColor.GRAY + " - Reload the config.");
            }
            if (sender.hasPermission("mobstacker.clearall")) {
                sender.sendMessage(ChatColor.YELLOW + "/mobstacker clearall" + ChatColor.GRAY + " - Remove all loaded stacks.");
            }
            if (!(sender instanceof Player)) {
                return true;
            }
            if (sender.hasPermission("mobstacker.inspect")) {
                sender.sendMessage(ChatColor.YELLOW + "/mobstacker inspect" + ChatColor.GRAY + " - Inspect MobStacker data on an entity.");
            }
            if (sender.hasPermission("mobstacker.merge")) {
                sender.sendMessage(ChatColor.YELLOW + "/mobstacker merge" + ChatColor.GRAY + " - Trigger a merge attempt for an entity.");
            }

            return true;
        }

        if (args[0].equals("reload") && sender.hasPermission("mobstacker.reload")) {
            getPlugin().reloadConfig();

            sender.sendMessage(ChatColor.GREEN + "Reloaded the config for MobStacker.");

            return true;
        }

        if (args[0].equals("toggle") && sender.hasPermission("mobstacker.toggle")) {
            getPlugin().setStacking(!getPlugin().isStacking());

            sender.sendMessage(ChatColor.GREEN + "Mob stacking is now " + (getPlugin().isStacking() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + ChatColor.GREEN + ".");

            return true;
        }

        if (args[0].equals("clearall") && sender.hasPermission("mobstacker.clearall")) {

            getPlugin().removeAllStacks();

            sender.sendMessage(ChatColor.GREEN + "Removed all existing stacks.");

            return true;
        }

        if (args[0].equals("inspect") && sender.hasPermission("mobstacker.inspect")) {
            LivingEntity entity = getTargetEntity(sender, command);
            if (entity == null) {
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "Inspecting " + ChatColor.GRAY + entity.getType().name() + ChatColor.YELLOW + ":");
            sender.sendMessage(ChatColor.YELLOW + "Max stack size: " + ChatColor.GRAY + getPlugin().getMaxStackSize(entity.getType()));
            sender.sendMessage(ChatColor.YELLOW + "Current stack size: " + ChatColor.GRAY + getPlugin().getStackUtils().getStackSize(entity));
            sender.sendMessage(ChatColor.YELLOW + "Stackable: " + ChatColor.GRAY + getPlugin().getStackUtils().isStackable(entity));
            sender.sendMessage(ChatColor.YELLOW + "Stack average health: " + ChatColor.GRAY + getPlugin().getStackUtils().getAverageHealth(entity, false));
            sender.sendMessage(ChatColor.YELLOW + "Breed timer up: " + ChatColor.GRAY + getPlugin().getStackUtils().canBreed(entity));
            return true;
        }

        if (args[0].equals("merge") && sender.hasPermission("mobstacker.merge")) {
            LivingEntity entity = getTargetEntity(sender, command);
            if (entity == null) {
                return true;
            }

            getPlugin().getStackUtils().attemptToStack(entity, 1);

            sender.sendMessage(ChatColor.GREEN + "Attempting to merge " + entity.getType().name() + "!");
            return true;
        }

        return false;
    }

    private @Nullable LivingEntity getTargetEntity(CommandSender sender, Command command) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(command.getUsage());
            return null;
        }

        Player player = (Player) sender;

        RayTraceResult rayTraceResult = player.rayTraceBlocks(16);
        Entity entity;
        if (rayTraceResult == null || !((entity = rayTraceResult.getHitEntity()) instanceof LivingEntity)) {
            sender.sendMessage(ChatColor.YELLOW + "Please aim at a living entity.");
            return null;
        }

        return (LivingEntity) entity;
    }

    private MobStacker getPlugin() {
        return this.plugin;
    }

}
