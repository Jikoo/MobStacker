package com.kiwifisher.mobstacker.commands;

import com.kiwifisher.mobstacker.MobStacker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MobStackerCommands implements CommandExecutor {

    private final MobStacker plugin;

    public MobStackerCommands(MobStacker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
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

        return false;
    }

    private MobStacker getPlugin() {
        return this.plugin;
    }

}
