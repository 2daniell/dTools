package com.daniel.indotools.inventories;

import com.daniel.indotools.Main;
import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.api.SkullCreator;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.hook.EconomyHook;
import com.daniel.indotools.menu.Menu;
import com.daniel.indotools.model.Pickaxe;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PickaxeInventory extends Menu {

    private static final int PRICE = 20000;

    public PickaxeInventory(Player player) {
        super(player, "   §7-== §bI§7n§bd§7o§bm§7i§bn§7u§bs §7N§be§7t§bw§7o§br§7k §7==-", 3*9);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("Comprar Picareta")) {

            double playerBalance = EconomyHook.getBalanceOf(player);

            if (playerBalance >= PRICE) {

                if(!isInventoryFull(player)) {

                    for (ItemStack item : player.getInventory().getContents()) {

                        if (item == null || item.getType() == Material.AIR) continue;

                        NBTItem nbtItem = new NBTItem(item);
                        nbtItem.hasTag("custompickaxeid");

                        player.closeInventory();
                        player.sendMessage("§cVocê só pode usar uma picareta por vez.");
                        return;

                    }
                    Pickaxe pickaxe = new Pickaxe();
                    PickaxeHandler.getPickaxes().add(pickaxe);
                    player.getInventory().addItem(pickaxe.getItem());
                    EconomyHook.removeCoins(player, PRICE);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    player.closeInventory();

                } else {
                    player.sendMessage(Main.config().getString("message.inventoryfull").replace('&', '§'));
                }

            } else {
                player.sendMessage(Main.config().getString("message.no-balance").replace('&', '§'));
            }
            return;
        }
        if (name.equalsIgnoreCase("Fechar")) {
            player.closeInventory();
        }

    }

    @Override
    public void setItens(Inventory inventory) {
        ItemStack X = new ItemBuilder(SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/ed0a1420844ce237a45d2e7e544d135841e9f82d09e203267cf8896c8515e360")).setName("§cFechar").build();
        ItemStack pickaxe = new ItemBuilder(Material.WOOD_PICKAXE).setName("§fComprar Picareta").setLore("","§7Custo: §a$ §f" + PRICE,"", "§7Click Aqui para pegar uma picareta").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).build();
        ItemStack info = new ItemBuilder(Material.NETHER_STAR).setName("§fInformações").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).build();
        ItemStack vidrociano = new ItemBuilder(Material.STAINED_GLASS_PANE,(short) 9).addFlags(ItemFlag.HIDE_ATTRIBUTES).setName("&f ").build();

        fill(inventory);
        inventory.setItem(22,X);
        inventory.setItem(11,pickaxe);

        inventory.setItem(4,info);
        inventory.setItem(1,vidrociano);
        inventory.setItem(9,vidrociano);
        inventory.setItem(19,vidrociano);
        inventory.setItem(7,vidrociano);
        inventory.setItem(17,vidrociano);
        inventory.setItem(25,vidrociano);
    }

    @Override
    public void fill(Inventory inventory) {
        int slots = 0;
        for (ItemStack items : inventory.getContents()) {
            if (items != null && items.getType() != Material.AIR) continue;
            inventory.setItem(slots++, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§f ").build());
        }
    }

    public boolean isInventoryFull(Player player) {
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return false;
            }
        }
        return true;
    }
}
