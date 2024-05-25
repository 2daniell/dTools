package com.daniel.indotools.listeners;

import com.daniel.indotools.Main;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.menu.Menu;
import com.daniel.indotools.model.Pickaxe;
import com.daniel.indotools.model.Trade;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.*;

public class Listeners implements Listener {

    private final Map<UUID, List<ItemStack>> pickaxes = new HashMap<>();

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
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (pickaxes.containsKey(e.getPlayer().getUniqueId())) {

            Player player = e.getPlayer();

            pickaxes.get(player.getUniqueId()).forEach(item -> player.getInventory().addItem(item));
            pickaxes.remove(player.getUniqueId());

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!Main.config().getBoolean("config.can-drop")) {

            ItemStack itemStack = e.getItemDrop().getItemStack();

            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            if (!nbtItem.hasTag("custompickaxeid")) return;

            e.setCancelled(true);

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (Main.config().getBoolean("config.keep-item")) {
            Player player = e.getEntity();

            List<ItemStack> list = new ArrayList<>();
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                NBTItem nbtItem = new NBTItem(itemStack);
                if (!nbtItem.hasTag("custompickaxeid")) continue;

                list.add(itemStack);

                e.getDrops().removeIf(drop -> drop.equals(itemStack));

            }
            pickaxes.put(player.getUniqueId(), list);
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
            if (pickaxe.isMaxLevel()) return;
            int toAdd = Manager.getXpBlock(e.getBlock());
            if (pickaxe.addXp(toAdd)) e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1f, 1f);
            if (pickaxe.canUpTier()) pickaxe.upTier(inHand);
            pickaxe.updateLore(inHand);
            return;
        }

        int xp = nbtItem.getInteger("custompickaxexp");
        int level = nbtItem.getInteger("custompickaxelevel");

        Pickaxe pic = new Pickaxe(id, level, xp);
        pic.setEnchantments(new HashMap<>(inHand.getEnchantments()));

        PickaxeHandler.getPickaxes().add(pic);

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
        if (e.getInventory().getHolder() instanceof Menu) return;

        InventoryType type = e.getInventory().getType();

        ClickType clickType = e.getClick();
        InventoryAction action = e.getAction();

        if (type == InventoryType.CRAFTING || type == InventoryType.ENDER_CHEST || type == InventoryType.CREATIVE ||
                type == InventoryType.PLAYER) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            if (e.getClickedInventory().getType() != InventoryType.PLAYER) {

                for (ItemStack item : e.getWhoClicked().getInventory().getContents()) {

                    if (item == null || item.getType() == Material.AIR) continue;

                    NBTItem item1 = new NBTItem(item);

                    if (!item1.hasTag("custompickaxeid")) continue;
                    e.setCancelled(true);
                    return;

                }
            }
            return;
        }

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
