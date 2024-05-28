package com.daniel.indotools.listeners;

import com.daniel.indotools.Main;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.handler.SkinHandler;
import com.daniel.indotools.menu.Menu;
import com.daniel.indotools.model.CustomEnchant;
import com.daniel.indotools.model.Pickaxe;
import com.daniel.indotools.model.Trade;
import com.daniel.indotools.objects.enums.SkinType;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();
        Inventory inventory = e.getView().getBottomInventory();
        ItemStack itemOnCursor = e.getCursor();
        ItemStack itemInteractedWith = e.getCurrentItem();

        if (itemInteractedWith == null || itemInteractedWith.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(itemInteractedWith);
        if (!nbtItem.hasTag("custompickaxeid")) return;

        if (Main.config().getBoolean("config.only-diamond")) {
            if (itemInteractedWith.getType() != Material.DIAMOND_PICKAXE) {
                return;
            }
        }

        if (e.getAction().equals(InventoryAction.SWAP_WITH_CURSOR) &&
                e.getClick().isRightClick() &&
                (inventory.getType().equals(InventoryType.PLAYER) ||
                        e.getInventory().getType().equals(InventoryType.CRAFTING))
                && (itemOnCursor.getType() == Material.ENCHANTED_BOOK)) {

            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) itemOnCursor.getItemMeta();
            if (bookMeta != null) {
                Pickaxe pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                if (pickaxe == null) {
                    UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));
                    int xp = nbtItem.getInteger("custompickaxexp");
                    int level = nbtItem.getInteger("custompickaxelevel");
                    SkinType type = SkinType.getSkinByLore(itemInteractedWith.getItemMeta().getLore());

                    Pickaxe pic = new Pickaxe(id, level, xp, type);
                    pic.setEnchantments(new HashMap<>(itemInteractedWith.getEnchantments()));

                    PickaxeHandler.getPickaxes().add(pic);
                    pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                }

                boolean allCustom = false;

                NBTItem cursor = new NBTItem(itemOnCursor);
                if (cursor.hasTag("allcustomenchant")) {
                    allCustom = true;
                }

                for (Enchantment enchant : bookMeta.getStoredEnchants().keySet()) {
                    if (pickaxe.getEnchantments().containsKey(enchant)) {
                        player.sendMessage("§cSua picareta ja possui esse encantamento");
                        continue;
                    }

                    if (enchant.equals(Enchantment.SILK_TOUCH)) {
                        pickaxe.silkTorch(itemInteractedWith);
                        break;
                    }

                    if (allCustom) {
                        pickaxe.setFullCustom(itemInteractedWith);
                        break;
                    }

                    int level = bookMeta.getStoredEnchants().get(enchant);
                    pickaxe.getEnchantments().put(enchant, level);
                    pickaxe.updateLore(itemInteractedWith);
                }
                player.setItemOnCursor(null);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSkin(BlockBreakEvent e) {
        ItemStack inHand = e.getPlayer().getItemInHand();
        if (!inHand.getType().name().endsWith("_PICKAXE")) return;
        NBTItem nbtItem = new NBTItem(inHand);
        if (!nbtItem.hasTag("custompickaxeid")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK ||
        e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            ItemStack inHand = e.getItem();

            if (inHand == null || !inHand.hasItemMeta() || !inHand.getItemMeta().hasDisplayName()) return;
            if (e.getItem().getType() != Material.DIAMOND_PICKAXE) return;
            Player player = e.getPlayer();

            NBTItem nbtItem = new NBTItem(inHand);
            if (!nbtItem.hasTag("customskin")) return;

            if (!player.isSneaking()) return;

            String name = ChatColor.stripColor(inHand.getItemMeta().getDisplayName());
            SkinType skinType = SkinType.getSkinByDisplayName(name);

            if (skinType == null) ;
            if (SkinHandler.hasSkin(player, skinType)) {
                player.sendMessage("§cVocê já possui essa skin!");
                return;
            }

            player.setItemInHand(null);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setskin " + player.getName() + " " + skinType.toString());

            player.sendMessage("§aSkin ativada!");
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
            if (pickaxe.addXp(toAdd, inHand))
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 1f, 1f);
            if (pickaxe.canUpTier()) pickaxe.upTier(inHand);
            pickaxe.updateLore(inHand);
            return;
        }

        int xp = nbtItem.getInteger("custompickaxexp");
        int level = nbtItem.getInteger("custompickaxelevel");

        SkinType skinType = SkinType.getSkinByLore(inHand.getItemMeta().getLore());

        Pickaxe pic = new Pickaxe(id, level, xp, skinType);
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

                    if (!e.getCurrentItem().getType().name().endsWith("_PICKAXE")) continue;
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
