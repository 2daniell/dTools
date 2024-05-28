package com.daniel.indotools.model;

import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.hook.EconomyHook;
import com.daniel.indotools.menu.Menu;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class Trade extends Menu {

    private final Player sender;
    private final ItemStack pickaxe;
    private final double value;

    public Trade(Player player, Player sender, ItemStack pickaxe, double value) {
        super(player, "Trade", 3*9);
        this.sender = sender;
        this.pickaxe = pickaxe;
        this.value = value;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("Recusar")) {
            sender.sendMessage("§cO jogador recusou sua solicitação.");
            player.closeInventory();
        } else if (name.equalsIgnoreCase("Aceitar")) {
            boolean hasItem = false;

            for (ItemStack item : sender.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR) continue;
                NBTItem nbt = new NBTItem(item);
                if (nbt.hasTag("custompickaxeid")) {
                    hasItem = true;
                    break;
                }
            }
            if (hasItem) {

                if (EconomyHook.getBalanceOf(player) >= value) {
                    player.closeInventory();
                    player.getInventory().addItem(pickaxe);
                    sender.getInventory().removeItem(pickaxe);

                    EconomyHook.removeCoins(player, value);
                    EconomyHook.depositCoins(sender, value);

                    sender.sendMessage("§aTroca realizada!");
                    player.sendMessage("§aTroca realizada!");

                } else {
                    player.sendMessage("§cVocê nao tem money suficiente, cancelando.");
                    sender.sendMessage("§f" + player.getName() + " §cnão tem money suficiente, cancelando.");
                    player.closeInventory();

                }

            } else {
                player.closeInventory();
                player.sendMessage("§cCancelado. O jogador solicitando não possui a picareta no inventário no momento.");

                sender.sendMessage("§cCancelado. Você não possui a picareta no seu inventário.");
            }
        }
    }

    @Override
    public void setItens(Inventory inventory) {

        fill(inventory);
        inventory.setItem(13, pickaxe);
        inventory.setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("§eInformações").setLore("§7Autor: §f" + sender.getName()
        , "§7Preço: §aR$" + formatDouble(value)).build());
        inventory.setItem(10, new ItemBuilder(Material.WOOL, (short)13).setDisplayName("§eAceitar").build());
        inventory.setItem(16, new ItemBuilder(Material.WOOL, (short) 14).setDisplayName("§eRecusar").build());
    }

    protected void fill(Inventory inventory) {
        ItemStack cyan = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 9)
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .setName("&f ")
                .build();

        ItemStack black = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .setDisplayName("§f ")
                .build();

        int[] cyanSlots = {1, 7, 9, 17, 19, 25};

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) continue;

            boolean isCyan = false;
            for (int slot : cyanSlots) {
                if (slot == i) {
                    isCyan = true;
                    break;
                }
            }

            if (isCyan) {
                inventory.setItem(i, cyan);
            } else {
                inventory.setItem(i, black);
            }
        }
    }

    public static String formatDouble(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.######");
        return decimalFormat.format(number);
    }

}
