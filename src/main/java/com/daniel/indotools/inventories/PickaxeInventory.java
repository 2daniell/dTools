package com.daniel.indotools.inventories;

import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.Arrays;

public class PickaxeInventory extends Menu {


    public PickaxeInventory(Player player) {
        super(player, player.getName() + " - Picaretas", 3*9);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("Picaretas")) {

        }
    }

    @Override
    public void setItens(Inventory inventory) {
        inventory.setItem(13, new ItemBuilder(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.SILK_TOUCH, 1)
                .addItemFlag(ItemFlag.HIDE_ENCHANTS).setDisplayName("§ePicaretas").setLore(
                        Arrays.asList(
                                " ",
                                "§7Para iniciar sua jornada na mineração porque não iniciar",
                                "§7da melhor forma? adquira uma picatera e a evolua de acordo",
                                "§7com o XP obtido durante a mineração!",
                                " ",
                                "§8Clique com o botão §8§nesquerdo §8para adquirir"
                        )).build());
    }
}
