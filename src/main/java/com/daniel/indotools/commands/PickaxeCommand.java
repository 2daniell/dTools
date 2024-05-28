package com.daniel.indotools.commands;

import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.inventories.PickaxeInventory;
import com.daniel.indotools.model.CustomEnchant;
import com.daniel.indotools.model.Pickaxe;
import com.daniel.indotools.objects.enums.SkinType;
import com.daniel.indotools.utils.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class PickaxeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (args.length == 0) {
            player.openInventory(new PickaxeInventory(player).getInventory());

            return true;
        }
        if (player.hasPermission("pickaxe.admin")) {

            if (args[0].equalsIgnoreCase("getfull")) {

                if (args.length < 2) {
                    player.sendMessage("§cUse, /picareta getfull <player>");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cJogador não encontrado");
                    return true;
                }

                if (!fitsItem(target)) {
                    player.sendMessage("§cJogador algo com inventario cheio");
                    return true;
                }

                target.getInventory().addItem(CustomEnchant.getAllBook());
                player.sendMessage("§aItem concedido");
                return true;


            } else if (args[0].equalsIgnoreCase("setfull")) {

                ItemStack inHand = player.getItemInHand();

                if(inHand == null || inHand.getType() == Material.AIR) {
                    player.sendMessage("§cItem invalido");
                    return true;
                }

                NBTItem nbtItem = new NBTItem(inHand);
                if (!nbtItem.hasTag("custompickaxeid")) {
                    player.sendMessage("§cItem invalido");
                    return true;
                }

                Pickaxe pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                if (pickaxe == null) {
                    UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));
                    int xp = nbtItem.getInteger("custompickaxexp");
                    int level = nbtItem.getInteger("custompickaxelevel");
                    SkinType type = SkinType.getSkinByLore(inHand.getItemMeta().getLore());

                    Pickaxe pic = new Pickaxe(id, level, xp, type);
                    pic.setEnchantments(new HashMap<>(inHand.getEnchantments()));

                    PickaxeHandler.getPickaxes().add(pic);
                    pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                }

                pickaxe.setFull(inHand);
                player.sendMessage("§aSetado no nivel maximo!");

            } else if (args[0].equalsIgnoreCase("silktouch")) {

                if (args.length < 2) {
                    player.sendMessage("§cUse, /picareta silktouch <player>");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cJogador não encontrado");
                    return true;
                }

                if (!fitsItem(target)) {
                    player.sendMessage("§cJogador algo com inventario cheio");
                    return true;
                }

                target.getInventory().addItem(Utils.getSilk());
                player.sendMessage("§aSilk touch dado ao jogador.");
                return true;

            } else if (args[0].equalsIgnoreCase("enchantgive")) {

                if (args.length < 3) {
                    player.sendMessage("§cUse, /enchantgive <enchant> <player>");
                    return true;
                }

                CustomEnchant enchant = Manager.findEnchantByName(args[1]);
                if (enchant == null) {
                    player.sendMessage("§cEncantamento não encontrado");
                    return true;
                }

                Player target = Bukkit.getPlayerExact(args[2]);
                if (target == null || !target.isOnline()) {
                    player.sendMessage("§cJogador não encontrado");
                    return true;
                }

                if (!fitsItem(target)) {
                    player.sendMessage("§cJogador alvo com inventario cheio");
                    return true;
                }

                target.getInventory().addItem(enchant.getBook());
                player.sendMessage("§aLivro encantado concedido.");
                return true;

            }

        }
        return true;
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
