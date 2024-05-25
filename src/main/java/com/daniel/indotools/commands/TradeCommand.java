package com.daniel.indotools.commands;

import com.daniel.indotools.Main;
import com.daniel.indotools.model.Trade;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TradeCommand implements CommandExecutor {

    private final Map<UUID, Trade> requests = new HashMap<>();
    private final List<UUID> waiting = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            player.sendMessage("§cUse, /trocarpicareta <player>");
            return true;
        }
        if (args[0].equalsIgnoreCase("accept")) {
            if (requests.containsKey(player.getUniqueId())) {

                Trade trade = requests.get(player.getUniqueId());

                Player target = trade.getPlayer();

                trade.setTarget(player);

                trade.start();

                requests.remove(player.getUniqueId());
                waiting.remove(player.getUniqueId());
                waiting.remove(target.getUniqueId());

            } else {
                player.sendMessage("Você não tem solicitações pendentes");
            }
        } else if(args[0].equalsIgnoreCase("recuse")) {
            if (requests.containsKey(player.getUniqueId())) {

                Player target = requests.get(player.getUniqueId()).getPlayer();
                target.sendMessage("§cO jogaador §f" + player.getName() + " §ccancelou" );
                player.sendMessage("§aVocê recusou a solicitação.");

                requests.remove(player.getUniqueId());
                waiting.remove(target.getUniqueId());
                waiting.remove(player.getUniqueId());
                return true;

            } else {
                player.sendMessage("Você não tem solicitações pendentes");
            }
        } else {

            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null || !target.isOnline()) {
                player.sendMessage("Jogador não encontrado");
                return true;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage("§cNão pode mandar solicitação para você mesmo");
                return true;
            }

            if (requests.containsKey(target.getUniqueId())) {
                player.sendMessage("§cO jogador já possui um pedido pendente, aguarde.");
                return true;
            }

            if (waiting.contains(player.getUniqueId())) {
                player.sendMessage("§cVocê possui uma solicitação pendente");
                return true;
            }

            Trade trade = new Trade(player);
            requests.put(target.getUniqueId(), trade);
            sendToTarget(target, player.getName());
            player.sendMessage("§aSolicitação enviada, o jogador possui 30 segundos para aceitar");

            waiting.add(player.getUniqueId());
            waiting.add(target.getUniqueId());

            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                requests.remove(target.getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (waiting.contains(player.getUniqueId()) || waiting.contains(target.getUniqueId())) {
                            player.sendMessage("§cTempo de solicitação expirado");
                            target.sendMessage("§cTempo de solicitação expirado");

                            waiting.remove(target.getUniqueId());
                            waiting.remove(player.getUniqueId());
                        }
                    }
                }.runTask(Main.getInstance());
            }, 20L * 30);

            return true;
        }
        return false;
    }

    private void sendToTarget(Player target, String sender) {

        TextComponent accept = new TextComponent("§a§lACEITAR");
        accept.setBold(true);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trocarpicareta accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
                new TextComponent("Clique para §aaceitar §fa solicitação de troca")
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
}
