package com.daniel.indotools.model;

import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.utils.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class Pickaxe {

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

    }

    private static final int MAX_LEVEL = 100;

    private final UUID id;
    private PickaxeType type;
    protected int level;
    private int xp;
    private final Set<Enchantment> enchantments;

    public Pickaxe() {
        this.id = UUID.randomUUID();
        this.level = 1;
        this.xp = 100;
        this.type = PickaxeType.WOOD;
        this.enchantments = new HashSet<>();
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(getMaterial());
        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setString("custompickaxeid", id.toString());

        ItemBuilder builder = new ItemBuilder(nbtItem.getItem()).setDisplayName(type.getDisplayName() + " " + Utils.getLevelColor(level));

        List<String> lore = new ArrayList<>();

        lore.add(0, " ");
        lore.add("§7Level §e" + level);
        lore.add("§7XP §e" + xp + "§f/§e" + getXpNextLevel());
        lore.add("§8[" + getBars() + "§8]");
        lore.add(" ");

        for (Enchantment enchant : enchantments) {
            if (enchant instanceof CustomEnchant) {
                CustomEnchant customEnchant = (CustomEnchant) enchant;
                builder.addEnchant(customEnchant, 1);
                lore.add(customEnchant.lore());
            }
        }

        return builder.setLore(lore).build();
    }

    public void addRandomVanilla() {

    }

    public int getXpNextLevel() { //XP ATUAL + 500 = Proximo
        if (level >= MAX_LEVEL) return 0;
        return xp + 500;
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
}
