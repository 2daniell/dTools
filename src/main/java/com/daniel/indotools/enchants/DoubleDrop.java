package com.daniel.indotools.enchants;

import com.daniel.indotools.Main;
import com.daniel.indotools.model.CustomEnchant;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DoubleDrop extends CustomEnchant {

    private static final String CHANCE_TAG = "enchantment_drop";

    private static final int MAX_CHANCE = Main.config().getInt("enchants.doubledrop.max-chance");

    public DoubleDrop() {
        super("Drop", 1238, 1, 1);

        add(BlockBreakEvent.class, this::onBreakMoney);
    }

    @Override
    public int getEnchantmentLevel(BlockExpEvent event) {
        return 1;
    }

    @Override
    protected String lore(ItemStack itemStack) {
        return "§b2X DROP " + getChance(itemStack) + "%";
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

    public void onBreakMoney(BlockBreakEvent e, int level) {

        ItemStack inHand = e.getPlayer().getItemInHand();

        if (inHand.getEnchantments().containsKey(this)) {

            int chance = getChance(inHand);
            int randomChance = new Random().nextInt(100) + 1;
            if (randomChance <= chance) {

                Player player = e.getPlayer();

                if (inHand.getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {

                    ItemStack silkItem = new ItemStack(e.getBlock().getType(), 2); //dobro
                    player.getInventory().addItem(silkItem);

                } else {

                    List<ItemStack> drops = e.getBlock().getDrops().stream().peek(drop -> drop.setAmount(drop.getAmount() * 2)).collect(Collectors.toList());
                    player.getInventory().addItem(drops.toArray(new ItemStack[0]));

                }

                if (Main.config().getBoolean("enchants.doubledrop.send-message")) {
                    player.sendMessage(Main.config().getString("enchants.doubledrop.message").replace('&', '§'));
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
    public ItemStack getBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.setDisplayName("      &7-== &bEncantamentos Customizados &7==-");
        meta.setLore(Arrays.asList("","&b2X DROP"));
        meta.addStoredEnchant(this, 1, true);
        book.setItemMeta(meta);
        return book;
    }
}