package com.daniel.indotools.enchants;

import com.daniel.indotools.Main;
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

import java.util.Random;
import java.util.UUID;

public class DoubleXP extends CustomEnchant {

    private static final String CHANCE_TAG = "enchantment_xp";
    private static final int MAX_CHANCE = Main.config().getInt("enchants.xp.max-chance");

    public DoubleXP() {
        super("XP", 1237, 1, 1);

        add(BlockBreakEvent.class, this::onBreakXP);
    }

    public void onBreakXP(BlockBreakEvent e, int level) {

        ItemStack inHand = e.getPlayer().getItemInHand();

        if (inHand.getEnchantments().containsKey(this)) {

            int chance = getChance(inHand);
            int randomChance = new Random().nextInt(100) + 1;
            if (randomChance <= chance) {
                NBTItem nbtItem = new NBTItem(inHand);

                if(!nbtItem.hasTag("custompickaxeid")) return;

                UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));

                Pickaxe pickaxe = PickaxeHandler.findPickaxeById(id);

                if (pickaxe == null) return;
                if(pickaxe.isMaxLevel()) return;
                Block originalBlock = e.getBlock();

                int xpToAdd = (Manager.getXpBlock(originalBlock));

                pickaxe.addXp(xpToAdd);


                if (Main.config().getBoolean("enchants.xp.send-message")) {

                    e.getPlayer().sendMessage(Main.config().getString("enchants.xp.message").replace('&', '§'));

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
        return "§b2X XP " + getChance(itemStack) + "%";
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
