package com.daniel.indotools.enchants;

import com.daniel.indotools.Main;
import com.daniel.indotools.model.CustomEnchant;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Explosion extends CustomEnchant {

    private static final int MAX_CHANCE = Main.config().getInt("enchants.explosion.max-chance");
    private static final String CHANCE_TAG = "enchantment_explosion";

    public Explosion() {
        super("Explosao" , 1235, 1, 1);

        add(BlockBreakEvent.class, this::onBreak);
    }

    public void onBreak(BlockBreakEvent e, int level) {

        ItemStack inHand = e.getPlayer().getItemInHand();

        int blocksBroken = 1;
        Player player = e.getPlayer();

        Block originalBlock = e.getBlock();

        if (inHand.getEnchantments().containsKey(this)) {

            int chance = getChance(inHand);
            int randomChance = new Random().nextInt(100) + 1;
            if (randomChance <= chance) {


                Material originalType = originalBlock.getType();

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++ ){
                        for (int z = -1; z <= 1; z++) {
                            Location loc = originalBlock.getLocation().clone().add(x, y, z);
                            Block block = loc.getBlock();
                            Material blockType = block.getType();
                            if (blockType.equals(Material.AIR) || blockType.equals(Material.BEDROCK)) continue;
                            if (!blockType.equals(originalType)) continue;

                            block.setType(Material.AIR);

                            blocksBroken++;

                        }
                    }
                }

                int finalBlocksBroken = blocksBroken;
                List<ItemStack> drops = e.getBlock().getDrops().stream().peek(drop -> drop.setAmount(drop.getAmount() * finalBlocksBroken)).collect(Collectors.toList());
                player.getInventory().addItem(drops.toArray(new ItemStack[0]));

                player.playSound(player.getLocation(), Sound.EXPLODE, 2f, 2f);

                if (Main.config().getBoolean("enchants.explosion.send-message")) {

                    e.getPlayer().sendMessage(Main.config().getString("enchants.explosion.message").replace('&', '§'));

                }
            }
        }
    }

    @Override
    public ItemStack getBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.setDisplayName("      &7-== &bEncantamentos Customizados &7==-");
        meta.addStoredEnchant(this, 1, true);
        meta.setLore(Arrays.asList("","&bEXPLOSIVE"));
        book.setItemMeta(meta);
        return book;
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
        return "§bEXPLOSIVE " + getChance(itemStack) + "%";
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
