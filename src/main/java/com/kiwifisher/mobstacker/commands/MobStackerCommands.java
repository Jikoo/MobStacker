package com.kiwifisher.mobstacker.commands;

import com.kiwifisher.mobstacker.MobStacker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MobStackerCommands implements CommandExecutor {

    private final MobStacker plugin;

    public MobStackerCommands(MobStacker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (command.getLabel().equalsIgnoreCase("mobstacker") && commandSender instanceof Player) {

            Player player = (Player) commandSender;

            if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
                player.sendMessage(ChatColor.GREEN + "Toggle: " + ChatColor.YELLOW + "/mobstacker toggle" + ChatColor.GRAY + " - Toggles whether mobs stack globally");
                player.sendMessage(ChatColor.GREEN + "Toggle: " + ChatColor.YELLOW + "/mobstacker reload" + ChatColor.GRAY + " - Reloads the config");

            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload") && player.hasPermission("mobstacker.reload")) {
                getPlugin().reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Reloaded the config for MobStacker");

            } else if (args.length == 1 && args[0].equalsIgnoreCase("toggle") && player.hasPermission("mobstacker.toggle")) {
                getPlugin().setStacking(!getPlugin().isStacking());

                player.sendMessage(ChatColor.GREEN + "Mob stacking is now " + (getPlugin().isStacking() ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));

            } if (args.length == 1 && args[0].equalsIgnoreCase("clearall") && player.hasPermission("mobstacker.clearall")) {

                getPlugin().removeAllStacks();

            } else {
                player.sendMessage(ChatColor.RED + "Unrecognised command. Please check /mobstacker help");
            }

        } else if (command.getLabel().equalsIgnoreCase("mobstacker") && commandSender instanceof ConsoleCommandSender) {

            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                getPlugin().reloadConfig();
                getPlugin().log("Reloaded the config for MobStacker");

            }

            if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
                getPlugin().setStacking(!getPlugin().isStacking());

                getPlugin().log("Mob stacking is now " + (getPlugin().isStacking() ? "enabled" : "disabled"));

            }

        }

        return false;
    }

    private MobStacker getPlugin() {
        return this.plugin;
    }

}
