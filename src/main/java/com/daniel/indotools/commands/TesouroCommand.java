package com.daniel.indotools.commands;

import com.daniel.indotools.handler.TreasureHandler;
import com.daniel.indotools.storage.Cache;
import com.daniel.indotools.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TesouroCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (player.hasPermission("indotools.admin")) {
            if (args.length == 0) {
                player.sendMessage("§4§lERRO §cUse, /itemtesouro <reload> ou /itemtesouro (add/remove) <nome>");

                for(ItemStack i : TreasureHandler.getItems()) {
                    player.getInventory().addItem(i);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length > 1) {

                    String name = StringUtils.join(args, " ", 1, args.length);

                    if (!name.isEmpty() && !Cache.exists(name)) {

                        ItemStack inHand = player.getItemInHand();

                        if(inHand != null && inHand.getType() != Material.AIR) {

                            Cache.addItem(name, Utils.write(inHand));

                            player.getInventory().remove(inHand);

                            player.sendMessage("§a§lSUCESSO §aItem adicionado!");
                            return true;
                        } else {
                            player.sendMessage("§4§lERRO §cItem invalido ");
                        }
                    } else {
                        player.sendMessage("§4§lERRO §cNome já existente ou invalido.");
                    }
                } else {
                    player.sendMessage("§4§lERRO §cUse, /itemtesouro <reload> ou /itemtesouro (add/remove) <nome>");
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length > 1) {

                    String name = StringUtils.join(args, " ", 1, args.length);

                    if (!name.isEmpty() && Cache.exists(name)) {

                        Cache.deleteItem(name);
                        player.sendMessage("§a§lSUCESSO §aItem removido.");

                    } else {
                        player.sendMessage("§4§lERRO §cItem não existente ou nome invalido.");
                    }
                } else {
                    player.sendMessage("§4§lERRO §cUse, /itemtesouro <reload> ou /itemtesouro (add/remove) <nome>");
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                TreasureHandler.load();
                player.sendMessage("§a§lSUCESSO §aTesouros recarregados.");
            }

        }
        return false;
    }
}
