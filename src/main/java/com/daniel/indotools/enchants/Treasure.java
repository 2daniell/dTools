package com.daniel.indotools.enchants;

import com.daniel.indotools.model.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Treasure extends CustomEnchant {

    private static final int MAX_CHANCE = 5;

    private final int chance;

    public Treasure() {
        super("Tesouro", 1256, 1, 1);
        this.chance = new Random().nextInt(MAX_CHANCE) + 1;
        add(BlockBreakEvent.class, this::onBreak);
    }

    public void onBreak(BlockBreakEvent e, int level) {
        if (e.getPlayer().getItemInHand().getEnchantments().containsKey(this)) {

            int randomChance = new Random().nextInt(100) + 1;
            if (randomChance <= chance) {

                //TESTE

            }
        }
    }

    @Override
    protected int getEnchantmentLevel(BlockExpEvent event) {
        return 1;
    }

    @Override
    protected String lore() {
        return "§cTesouro " + chance + "%";
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
}
