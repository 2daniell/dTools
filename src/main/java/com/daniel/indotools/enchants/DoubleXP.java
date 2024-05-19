package com.daniel.indotools.enchants;

import com.daniel.indotools.model.CustomEnchant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;

public class DoubleXP extends CustomEnchant {

    public DoubleXP(String name, int id, int maxLevel, int startLevel) {
        super(name, id, maxLevel, startLevel);
    }

    @Override
    protected int getEnchantmentLevel(BlockExpEvent event) {
        return 0;
    }

    @Override
    protected String lore() {
        return null;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return false;
    }
}
