package com.daniel.indotools.commands;

import com.daniel.indotools.handler.SkinHandler;
import com.daniel.indotools.model.Skin;
import com.daniel.indotools.objects.enums.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (sender.hasPermission("pickaxe.admin")) {

            if (args.length == 0) {
                sender.sendMessage("§cUse, /setskin <player> <skin>");
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

                Skin skin = new Skin(target.getUniqueId(), type);
                SkinHandler.addSkin(skin);

                sender.sendMessage("§aSkin concedida!");
                return true;


            } else {
                sender.sendMessage("§cUse, /setskin <player> <skin>");
            }

        }
        return false;
    }
}
