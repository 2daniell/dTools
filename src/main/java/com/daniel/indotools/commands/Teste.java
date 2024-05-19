package com.daniel.indotools.commands;

import com.daniel.indotools.handler.EnchantmentHandler;
import com.daniel.indotools.model.CustomEnchant;
import com.daniel.indotools.model.Pickaxe;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Teste implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length == 0) {
            Pickaxe pickaxe = new Pickaxe();
            pickaxe.getEnchantments().add(EnchantmentHandler.findByName("Explosão"));

            player.getInventory().addItem(pickaxe.getItem());

            return true;
        }
        return false;
    }
}
