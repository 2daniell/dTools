package com.daniel.indotools.enchants;

import com.daniel.indotools.Main;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.model.CustomEnchant;
import com.daniel.indotools.model.Pickaxe;
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

import java.util.Random;
import java.util.UUID;

public class Explosion extends CustomEnchant {

    private static final int MAX_CHANCE = Main.config().getInt("enchants.explosion.max-chance");
    private final PickaxeHandler handler;
    private final int chance;

    public Explosion(PickaxeHandler handler) {
        super("Explosão" , 1235, 1, 1);
        this.handler = handler;
        this.chance = new Random().nextInt(MAX_CHANCE) + 1;
        add(BlockBreakEvent.class, this::onBreak);
    }

    public void onBreak(BlockBreakEvent e, int level) {

        ItemStack inHand = e.getPlayer().getItemInHand();

        int blocksBroken = 1;
        Player player = e.getPlayer();

        Block originalBlock = e.getBlock();

        if (inHand.getEnchantments().containsKey(this)) {

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

                            block.breakNaturally();

                            blocksBroken++;

                        }
                    }
                }
                player.playSound(player.getLocation(), Sound.EXPLODE, 2f, 2f);
            }

            NBTItem nbtItem = new NBTItem(inHand);

            if(!nbtItem.hasTag("custompickaxeid")) return;

            UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));

            Pickaxe pickaxe = handler.findPickaxeById(id);

            if (pickaxe == null) return;

            int xpToAdd = Manager.getXpBlock(originalBlock);

            pickaxe.addXp(xpToAdd);
            pickaxe.updateLore(inHand);

            if (Main.config().getBoolean("enchants.explosion.remove-durability")) {

                short durabilityLoss = (short) blocksBroken;

                if (inHand.getDurability() + durabilityLoss >= inHand.getType().getMaxDurability()) {
                    player.getInventory().setItemInHand(null);
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1f, 1f);
                } else {
                    inHand.setDurability((short) (inHand.getDurability() + durabilityLoss));
                }
            }
        }
    }

    @Override
    protected int getEnchantmentLevel(BlockExpEvent event) {
        return 1;
    }

    @Override
    protected String lore() {
        return "§cExplosão " + chance + "%";
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
