package com.daniel.indotools.model;

import com.daniel.indotools.Main;
import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.utils.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.*;

@Getter
public class Pickaxe implements Serializable {

    @Getter
    @AllArgsConstructor
    private enum PickaxeType {

        WOOD(0, 19, Material.WOOD_PICKAXE, "§fPicareta"),
        STONE(20, 39, Material.STONE_PICKAXE, "§fPicareta"),
        GOLD(40, 59, Material.GOLD_PICKAXE, "§ePicareta"),
        IRON(60, 79, Material.IRON_PICKAXE, "§ePicareta"),
        DIAMOND(80, 100, Material.DIAMOND_PICKAXE, "§bPicareta");

        private final int minLevel;
        private final int maxLevel;
        private final Material material;
        private final String displayName;


        public static Material getMaterialByLevel(int level) {
            for (PickaxeType pickaxeType : values()) {
                if (level >= pickaxeType.minLevel && level <= pickaxeType.maxLevel) {
                    return pickaxeType.getMaterial();
                }
            }
            return PickaxeType.WOOD.material;
        }

        public static PickaxeType getByLevel(int level) {
            for (PickaxeType pickaxeType : values()) {
                if (level >= pickaxeType.minLevel && level <= pickaxeType.maxLevel) {
                    return pickaxeType;
                }
            }
            return PickaxeType.WOOD;
        }

    }

    private static final int MAX_LEVEL = 100;
    private static final int LEVEL = Main.config().getInt("config.level-xp");

    private UUID owner;

    private final UUID id;
    private PickaxeType type;
    protected int level;
    private int xp;

    @Setter
    private Set<Enchantment> enchantments;

    public Pickaxe(UUID owner, UUID id, int level, int xp) {
        this.owner = owner;
        this.id = id;
        this.type = PickaxeType.getByLevel(level);
        this.level = level;
        this.xp = xp;
    }

    public Pickaxe(UUID owner) {
        this.owner = owner;
        this.id = UUID.randomUUID();
        this.level = 99;
        this.xp = 0;
        this.type = PickaxeType.DIAMOND;
        this.enchantments = new HashSet<>();
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemBuilder(getMaterial()).setDisplayName(type.getDisplayName() + " " + Utils.getLevelColor(level)).build();

        updateLore(itemStack);

        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setString("custompickaxeid", id.toString());
        nbtItem.setInteger("custompickaxexp", 0);
        nbtItem.setInteger("custompickaxelevel", 1);
        nbtItem.applyNBT(itemStack);

        return itemStack;
    }

    public void addRandomVanilla() {

    }

    public boolean addXp(int xpToAdd) {
        if (level >= MAX_LEVEL) {
            return false;
        }

        this.xp += xpToAdd;

        if (xp >= getXpNextLevel()) {
            level++;
            xp = 0;

            return true;
        }

        return false;
    }

    public int getXpNextLevel() {
        if (level >= MAX_LEVEL) return 0;
        return level * LEVEL;
    }

    private String getBars() {
        int barsCount = 50;
        int completedBars = (int) Math.ceil((double) xp / getXpNextLevel() * barsCount);
        StringBuilder bars = new StringBuilder();

        for (int i = 0; i < completedBars; i++) {
            bars.append("§a|");
        }

        for (int i = completedBars; i < barsCount; i++) {
            bars.append("§c|");
        }

        return bars.toString();
    }

    private Material getMaterial() {
        return PickaxeType.getMaterialByLevel(getLevel());
    }

    private static List<Enchantment> getEnchantVanilaAvaliables() {
        List<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchant : Enchantment.values()) {
            if (enchant.getItemTarget()  == EnchantmentTarget.TOOL) {
                enchantments.add(enchant);
            }
        }
        return enchantments;
    }

    public void updateLore(ItemStack itemStack) {
        ItemMeta builder = itemStack.getItemMeta();
        if (builder.hasLore()) builder.getLore().clear();

        List<String> lore = new ArrayList<>();

        lore.add(0, " ");
        lore.add("§7Level §e" + level);
        lore.add("§7XP §e" + xp + "§f/§e" + getXpNextLevel());
        lore.add("§8[" + getBars() + "§8]");
        lore.add(" ");

        for (Enchantment enchant : enchantments) {
            if (enchant instanceof CustomEnchant) {
                CustomEnchant customEnchant = (CustomEnchant) enchant;
                builder.addEnchant(customEnchant, 1, true);
                lore.add(customEnchant.lore());
            }
        }

        builder.setLore(lore);
        itemStack.setItemMeta(builder);
        updateNBT(itemStack);
    }

    private void updateNBT(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("custompickaxexp", xp);
        nbtItem.setInteger("custompickaxelevel", level);
        nbtItem.applyNBT(itemStack);
    }
}
