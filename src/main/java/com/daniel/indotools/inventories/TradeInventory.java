package com.daniel.indotools.inventories;

import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class TradeInventory extends Menu {

    public TradeInventory(Player player) {
        super(player, "Trade", 3*9);
    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }

    @Override
    public void setItens(Inventory inventory) {
        ItemStack pane = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) )
        fill(inventory);


    }
}
