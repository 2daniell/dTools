package com.daniel.indotools.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Listeners implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack craftedItem = event.getCurrentItem();
        if (craftedItem.getType() == Material.DIAMOND_PICKAXE ||
                craftedItem.getType() == Material.IRON_PICKAXE ||
                craftedItem.getType() == Material.GOLD_PICKAXE ||
                craftedItem.getType() == Material.STONE_PICKAXE ||
                craftedItem.getType() == Material.WOOD_PICKAXE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (player.getItemInHand() == null || !player.getItemInHand().hasItemMeta()) return;

        NBTItem nbtItem = new NBTItem(player.getItemInHand());
        String isPickaxe = nbtItem.getString("custompickaxeid");

        if(isPickaxe == null) return;

        UUID id = UUID.fromString(isPickaxe);

        player.sendMessage("ID: " + id.toString());
    }
}
