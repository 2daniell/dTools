package com.daniel.indotools.commands;

import com.daniel.indotools.inventories.PickaxeInventory;
import com.daniel.indotools.model.Pickaxe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class PickaxeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (args.length == 0) {
            player.openInventory(new PickaxeInventory(player).getInventory());

            return true;
        }
        return false;
    }
}
