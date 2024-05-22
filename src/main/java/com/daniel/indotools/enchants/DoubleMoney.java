package com.daniel.indotools.enchants;

import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.hook.EconomyHook;
import com.daniel.indotools.model.CustomEnchant;
import com.daniel.indotools.model.Pickaxe;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DoubleMoney extends CustomEnchant {

    public DoubleMoney() {
        super("Money", 1238, 1, 1);

        add(BlockBreakEvent.class, this::onBreakMoney);
    }

    public void onBreakMoney(BlockBreakEvent e, int level) {

        ItemStack inHand = e.getPlayer().getItemInHand();

        if (inHand.getEnchantments().containsKey(this)) {

            double moneyToAdd = (Manager.getMoneyBlock(e.getBlock())) * 2;

            EconomyHook.depositCoins(e.getPlayer(), moneyToAdd);

        }
    }

    @Override
    protected int getEnchantmentLevel(BlockExpEvent event) {
        return 1;
    }

    @Override
    protected String lore(ItemStack itemStack) {
        return "§cMoney 2X";
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
