package com.daniel.indotools.commands;

import com.daniel.indotools.objects.enums.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SkinItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender.hasPermission("pickaxe.admin")) {

            if (args.length == 0) {
                sender.sendMessage("§cUse, /giveskin <player> <skin>");
                return true;
            }
            if (args.length > 1) {

                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null || !target.isOnline()) {
                    sender.sendMessage("§cJogador invalido");
                    return true;
                }

                SkinType type;
                try {
                    type = SkinType.valueOf(args[1].toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage("§cSkin invalida");
                    return true;
                }

                if (!fitsItem(target)) {
                    sender.sendMessage("Jogador com inventario cheio");
                    return true;
                }

                target.getInventory().addItem(type.getItem());
                target.sendMessage("§aVocê ganhou um ativador de skin");

                sender.sendMessage("§aAtivador de skin dado com sucesso");
                return true;
            } else {
                sender.sendMessage("§cUse, /giveskin <player> <skin>");
            }
        }
        return false;
    }

    private static boolean fitsItem(Player player) {
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return true;
            }
        }

        return false;
    }
}
