package com.daniel.indotools.enchants;

import com.daniel.indotools.Main;
import com.daniel.indotools.model.CustomEnchant;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

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

                List<ItemStack> drops = (List<ItemStack>) e.getBlock().getDrops();
                for (ItemStack drop : drops) {
                    drop.setAmount(drop.getAmount() * 2);
                }

                if (Main.config().getBoolean("enchants.doubledrop.send-message")) {

                    e.getPlayer().sendMessage(Main.config().getString("enchants.doubledrop.message").replace('&', '§'));

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
}