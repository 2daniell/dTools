package com.daniel.indotools.menu;

import com.daniel.indotools.api.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public abstract class Menu implements InventoryHolder {

    protected final Player player;
    private final Inventory inventory;

    public Menu(Player player, String name, int size){
        this.player = player;
        this.inventory = Bukkit.createInventory(this, size, name);
    }

    public abstract void onClick(InventoryClickEvent e);
    public abstract void setItens(Inventory inventory);

    @Override
    public Inventory getInventory() {
        setItens(inventory);
        return inventory;
    }

    protected void fill(Inventory inventory) {
        int slots = 0;
        for (ItemStack items : inventory.getContents()) {
            if (items != null && items.getType() != Material.AIR) continue;
            inventory.setItem(slots++, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§f ").build());
        }
    }
}
