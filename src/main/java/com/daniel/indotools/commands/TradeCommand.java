package com.daniel.indotools.commands;

import com.daniel.indotools.Main;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.handler.SkinHandler;
import com.daniel.indotools.model.Pickaxe;
import com.daniel.indotools.model.Trade;
import com.daniel.indotools.objects.Pair;
import com.daniel.indotools.objects.Triple;
import com.daniel.indotools.objects.enums.SkinType;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeCommand implements CommandExecutor {

    private static final Map<Integer, Double> tierPrices = new HashMap<>();
    private final Map<UUID, Triple<UUID, ItemStack, Double>> trade = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage("§cUse, /trocarpicareta <player> <valor>");
            return true;
        }

        if (args[0].equalsIgnoreCase("recuse")) {
            if (trade.containsKey(player.getUniqueId())) {

                Player senderTrade = Bukkit.getPlayer(trade.get(player.getUniqueId()).getFirst());
                senderTrade.sendMessage("§cO jogador §f" + player.getName() + " §ccancelou.");

                player.sendMessage("§aVocê cancelou o pedido.");

                trade.remove(player.getUniqueId());

            } else {
                player.sendMessage("§cVocê não possui solicitaçoes pendentes");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (trade.containsKey(player.getUniqueId())) {

                Player senderTrade = Bukkit.getPlayer(trade.get(player.getUniqueId()).getFirst());
                ItemStack pickaxe = trade.get(player.getUniqueId()).getSecond();
                double price = trade.get(player.getUniqueId()).getThird();

                for(ItemStack item : player.getInventory().getContents()) {
                    if (item == null || item.getType() == Material.AIR) continue;
                    NBTItem nbt = new NBTItem(item);
                    if (!nbt.hasTag("custompickaxeid")) continue;

                    senderTrade.sendMessage("§cO jogador já possui uma picareta no inventario.");
                    player.sendMessage("§cVocê possui uma picareta no inventario. Trade cancelado.");
                    trade.remove(player.getUniqueId());
                    return true;
                }

                if (!fitsItem(player)) {
                    player.sendMessage("§cVocê está com o inventario cheio. Cancelado.");
                    trade.remove(player.getUniqueId());
                    return true;
                }

                player.openInventory(new Trade(player, senderTrade, pickaxe, price).getInventory());
                trade.remove(player.getUniqueId());

            } else {
                player.sendMessage("§cVocê não possui solicitaçoes pendentes");
            }
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUse, /trocarpicareta <player> <valor>");
            return true;
        }

        ItemStack inHand = player.getItemInHand();
        if(inHand == null || inHand.getType() == Material.AIR) {
            player.sendMessage("§cNenhuma picareta encontrada em sua mão");
            return true;
        }

        NBTItem nbtItem = new NBTItem(inHand);
        if (!nbtItem.hasTag("custompickaxeid")) {
            player.sendMessage("§cItem invalido");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cJogador invalido");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage("§cVoce não pode pode enviar solicitação para você mesmo");
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

        pickaxe.setSkin(SkinType.DEFAULT, inHand);

        if (!pickaxe.hasPermission(target)) {
            player.sendMessage("§cO jogador não possui permissão para possuir esse tier");
            return true;
        }

        double price;
        try {
            price = Double.parseDouble(args[1]);
        } catch (Exception e) {
            player.sendMessage("§cO numero não é valido");
            return true;
        }


        double pickaxePrice = tierPrices.getOrDefault((pickaxe.getTier() == 0) ? 1 : pickaxe.getTier(), 0.0);

        if (price < pickaxePrice) {
            player.sendMessage("§cO valor minimo do seu tier é §f" + pickaxePrice);
            return true;
        }

        if (trade.containsKey(target.getUniqueId())) {
            player.sendMessage("§cO jogador já esta em processo de trade. Aguarde.");
            return true;
        }

        trade.put(target.getUniqueId(), new Triple<>(player.getUniqueId(), inHand, price));
        sendToTarget(target, player.getName());
        player.sendMessage("§aSolicitação enviada para §f" + target.getName() + " §a, ele tem 30 segundos para aceitar.");

        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (trade.containsKey(target.getUniqueId())) {
                        trade.remove(target.getUniqueId());
                        player.sendMessage("§cTempo de solicitação expirado");
                        target.sendMessage("§cTempo de solicitação expirado");
                    }
                }
            }.runTask(Main.getInstance());
        }, 20L * 30);

        return true;
    }

    private void sendToTarget(Player target, String sender) {

        TextComponent accept = new TextComponent("§a§lVER MAIS");
        accept.setBold(true);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trocarpicareta accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
                new TextComponent("Clique para §aver mais §fda solicitação de troca")
        }));

        TextComponent recuse = new TextComponent("§c§lRECUSAR");
        recuse.setBold(true);
        recuse.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trocarpicareta recuse"));
        recuse.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
                new TextComponent("Clique para §crecusar §fa solicitação de troca")
        }));

        TextComponent firstPart = new TextComponent("§7Clique aqui para ");
        TextComponent segundPart = new TextComponent(" §7ou ");
        TextComponent tpart = new TextComponent(" §7a solicitação");

        firstPart.addExtra(accept);
        firstPart.addExtra(segundPart);
        firstPart.addExtra(recuse);
        firstPart.addExtra(tpart);

        target.sendMessage(new String[]{"", ""});
        target.spigot().sendMessage(new TextComponent("§eO jogador §f" + sender + " §elhe enviou um trade."));
        target.spigot().sendMessage(firstPart);

    }

    public static void loadPrices() {
        for(String key : Main.config().getConfigurationSection("tier").getKeys(false)) {

            int tier = Main.config().getInt("tier." + key + ".tier");
            double value = Main.config().getDouble("tier." + key + ".price");

            tierPrices.put(tier, value);
        }
    }

    public static boolean fitsItem(Player player) {
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return true;
            }
        }

        return false;
    }
}
