package com.daniel.indotools.listeners;

import com.daniel.indotools.Main;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.hook.EconomyHook;
import com.daniel.indotools.menu.Menu;
import com.daniel.indotools.model.CustomEnchant;
import com.daniel.indotools.model.Pickaxe;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.stream.Collectors;

public class Listeners implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack craftedItem = e.getCurrentItem();
        if (craftedItem.getType() == Material.DIAMOND_PICKAXE ||
                craftedItem.getType() == Material.IRON_PICKAXE ||
                craftedItem.getType() == Material.GOLD_PICKAXE ||
                craftedItem.getType() == Material.STONE_PICKAXE ||
                craftedItem.getType() == Material.WOOD_PICKAXE) {
            e.setCancelled(true);
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        for (ItemStack itemStack : player.getInventory().getContents()) {

            NBTItem nbtItem = new NBTItem(itemStack);
            if (!nbtItem.hasTag("custompickaxeid")) continue;



        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreakBlock(BlockBreakEvent e) {
        if (!Manager.isWorld(e.getBlock().getLocation().getWorld().getName())) return;
        ItemStack inHand = e.getPlayer().getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(inHand);
        if (!nbtItem.hasTag("custompickaxeid")) return;

        UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));

        Pickaxe pickaxe = PickaxeHandler.findPickaxeById(id);
        if (pickaxe != null) {
            pickaxe.updateLore(inHand);

            double moneyToAdd = (Manager.getMoneyBlock(e.getBlock())) * 2;
            System.out.println("AQUI");
            EconomyHook.depositCoins(e.getPlayer(), moneyToAdd);

            return;
        }

        int xp = nbtItem.getInteger("custompickaxexp");
        int level = nbtItem.getInteger("custompickaxelevel");

        Pickaxe pic = new Pickaxe(e.getPlayer().getUniqueId(), id, level, xp);
        pic.setEnchantments(inHand.getEnchantments());

        PickaxeHandler.getPickaxes().add(pic);

    }

    @EventHandler
    public void onXpManager(BlockBreakEvent e) {
        ItemStack inHand = e.getPlayer().getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) return;
        if (!CustomEnchant.hasCustomEnchant(inHand)) return;



    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (!(holder instanceof Menu)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        ((Menu) holder).onClick(e);
    }

    @EventHandler
    public void swap(InventoryClickEvent e) {

        InventoryType type = e.getInventory().getType();

        ClickType clickType = e.getClick();
        InventoryAction action = e.getAction();

        if (type == InventoryType.CRAFTING || type == InventoryType.ENDER_CHEST || type == InventoryType.CREATIVE ||
        type == InventoryType.PLAYER) return;

        ItemStack clicked = e.getCurrentItem();
        int hotbarButton = e.getHotbarButton();
        if (hotbarButton >= 0 && hotbarButton <= 8) {
            ItemStack hotBar = e.getWhoClicked().getInventory().getItem(hotbarButton);
            if (hotBar != null) {

                NBTItem nbtItem = new NBTItem(hotBar);
                if (!nbtItem.hasTag("custompickaxeid")) return;
                if (clickType == ClickType.NUMBER_KEY && (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.COLLECT_TO_CURSOR)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        if (clicked == null || clicked.getType() == Material.AIR) return;


        NBTItem nbtItem = new NBTItem(clicked);
        if (!nbtItem.hasTag("custompickaxeid")) return;


        if (clickType == ClickType.NUMBER_KEY && (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.COLLECT_TO_CURSOR)) {
            e.setCancelled(true);
        }

        e.setCancelled(true);
    }

}
