package com.daniel.indotools.enchants;

import com.daniel.indotools.Main;
import com.daniel.indotools.handler.TreasureHandler;
import com.daniel.indotools.model.CustomEnchant;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Treasure extends CustomEnchant {

    private static final int MAX_CHANCE = Main.config().getInt("enchants.treasure.max-chance");
    private static final String CHANCE_TAG = "enchantment_treasure";
    public Treasure() {
        super("Tesouro", 1236, 1, 1);
        add(BlockBreakEvent.class, this::onBreak);
    }

    public void onBreak(BlockBreakEvent e, int level) {
        if (e.getPlayer().getItemInHand().getEnchantments().containsKey(this)) {

            ItemStack inHand = e.getPlayer().getItemInHand();

            int chance = getChance(inHand);
            int randomChance = new Random().nextInt(100) + 1;
            if (randomChance <= chance) {

                ItemStack item = TreasureHandler.getRandom();

                if (!isInventoryFull(e.getPlayer())) {

                    Player player = e.getPlayer();

                    player.getInventory().addItem(item);


                    if (Main.config().getBoolean("enchants.treasure.send-message")) {

                        e.getPlayer().sendMessage(Main.config().getString("enchants.treasure.message").replace('&', '§'));

                    }

                }

            }

        }
    }

    public int getChance(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasTag(CHANCE_TAG)) {
            return nbtItem.getInteger(CHANCE_TAG);
        } else {
            int chance = new Random().nextInt(MAX_CHANCE) + 1;
            nbtItem.setInteger(CHANCE_TAG, chance);
            nbtItem.applyNBT(itemStack);
            return chance;
        }
    }

    @Override
    protected int getEnchantmentLevel(BlockExpEvent event) {
        return 1;
    }

    @Override
    protected String lore(ItemStack itemStack) {
        return "§bTESOURO " + getChance(itemStack) + "%";
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        Material material = item.getType();
        return (material == Material.DIAMOND_PICKAXE) || (material == Material.GOLD_PICKAXE) || (material == Material.IRON_PICKAXE) ||
                (material == Material.WOOD_PICKAXE) || (material == Material.STONE_PICKAXE);
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
