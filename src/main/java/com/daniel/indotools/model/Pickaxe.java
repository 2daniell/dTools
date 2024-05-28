package com.daniel.indotools.model;

import com.daniel.indotools.Main;
import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.objects.enums.SkinType;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.*;

@Getter
public class Pickaxe implements Serializable {

    @Getter
    @AllArgsConstructor
    public enum PickaxeType {

        WOOD(0, Material.WOOD_PICKAXE, "&fPicareta &8[&7Tier &lI&8]", "pickaxe.tier1"),
        GOLD(1, Material.GOLD_PICKAXE, "&fPicareta &8[&eTier &lII&8]", "pickaxe.tier2"),
        STONE(2, Material.STONE_PICKAXE, "&9Picareta &8[&fTier &9&lIII&8]", "pickaxe.tier3"),
        IRON(3, Material.IRON_PICKAXE, "&3Picareta &8[&fTier &3&lIV&8]", "pickaxe.tier4"),
        DIAMOND(4, Material.DIAMOND_PICKAXE, "&ePicareta &8[&6Tier &lV&8]", "pickaxe.tier5"),
        DIAMONDD(5, Material.DIAMOND_PICKAXE, "&ePicareta &8[&6Tier &lV&8]", "pickaxe.tier5");


        private int tier;
        private final Material material;
        private final String displayName;
        private final String permission;

        public static Material getMaterialByTier(int tier) {
            for (PickaxeType type : values()) {
                if (type.getTier() == tier) return type.getMaterial();
            }
            return null;
        }

        public static PickaxeType getTypeByLevel(int level) {
            int UP_TIER = Main.config().getInt("config.level-up-tier");
            int tier = (level / UP_TIER);
            for (PickaxeType type : values()) {
                if (type.getTier() == tier) return type;
            }
            return null;
        }
    }

    private static final int MAX_LEVEL = 100;
    private static final int UP_TIER = Main.config().getInt("config.level-up-tier");
    private static final int LEVEL = Main.config().getInt("config.level-xp");
    private static final int LEVEL_VANILLA_ENCHANT = Main.config().getInt("config.level-vanilla-enchant");
    private static final int MAX_LEVEL_ENCHANTS = Main.config().getInt("config.max-level-enchants");
    private static final double CUSTOM_ENCHANT_CHANCE = Main.config().getDouble("config.custom-enchant-chance");
    private static final int CUSTOM_LEVEL = Main.config().getInt("config.custom-level");
    private static final double SILK_MAX_CHANCE = Main.config().getInt("config.silk-max-chance");
    private static final List<Enchantment> VANILLA_ENCHANTS = getEnchantVanilaAvaliables();
    private static final List<CustomEnchant> CUSTOM_ENCHANTS = getCustomEnchantments();

    private final UUID id;
    private PickaxeType type;
    protected int level;
    private int xp;

    @Setter
    private Map<Enchantment, Integer> enchantments;

    private SkinType skinType;

    public Pickaxe(UUID id, int level, int xp, SkinType skinType) {
        this.id = id;
        this.type = PickaxeType.getTypeByLevel(level);
        this.level = level;
        this.xp = xp;
        this.skinType = skinType;
    }

    public Pickaxe() {
        this.id = UUID.randomUUID();
        this.level = 1;
        this.xp = 0;
        this.type = PickaxeType.WOOD;
        this.enchantments = new HashMap<>();
        this.skinType = SkinType.DEFAULT;
    }

    public void setFull(ItemStack itemStack) {
        this.xp = 0;
        this.level = MAX_LEVEL;
        this.type = PickaxeType.getTypeByLevel(level);
        itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment);

        setAllEnchant();
        updateNBT(itemStack);
        update(itemStack);
        setAllChance(itemStack);
        updateLore(itemStack);
    }

    public void setSkin(SkinType skinType, ItemStack itemStack) {
        this.skinType = skinType;
        updateLore(itemStack);
    }

    private void setAllEnchant() {
        this.enchantments.clear();
        for (Enchantment enchantment : getEnchantVanilaAvaliables()) {
            if (enchantment.equals(Enchantment.SILK_TOUCH)) continue;
            enchantments.put(enchantment, MAX_LEVEL_ENCHANTS);
        }

        for (CustomEnchant enchant : getCustomEnchantments()) {
            enchantments.put(enchant, 1);
        }

    }

    public void setFullCustom(ItemStack itemStack) {
        enchantments.forEach((enchantment, level) -> {
            if (enchantment instanceof CustomEnchant) {
                itemStack.removeEnchantment(enchantment);
            }
        });

        for (CustomEnchant enchant : getCustomEnchantments()) {
            enchantments.put(enchant, 1);
        }

        setAllChance(itemStack);
        updateLore(itemStack);
    }

    private void setAllChance(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("enchantment_drop", Main.config().getInt("enchants.doubledrop.max-chance"));
        nbtItem.setInteger("enchantment_xp", Main.config().getInt("enchants.xp.max-chance"));
        nbtItem.setInteger("enchantment_explosion", Main.config().getInt("enchants.explosion.max-chance"));
        nbtItem.setInteger("enchantment_treasure", Main.config().getInt("enchants.treasure.max-chance"));
        nbtItem.applyNBT(itemStack);
    }

    public void silkTorch(ItemStack stack) {
        if (enchantments.containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
            enchantments.remove(Enchantment.LOOT_BONUS_BLOCKS);
            stack.removeEnchantment(Enchantment.LOOT_BONUS_BLOCKS);
        }
        enchantments.put(Enchantment.SILK_TOUCH, 1);
        updateLore(stack);
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemBuilder(getMaterial()).addFlags(ItemFlag.HIDE_ATTRIBUTES).addFlags(ItemFlag.HIDE_UNBREAKABLE).setDisplayName(ChatColor.translateAlternateColorCodes('&', type.getDisplayName())).setUnbreakable().build();

        updateLore(itemStack);

        NBTItem nbtItem = new NBTItem(itemStack);

        nbtItem.setString("custompickaxeid", id.toString());
        nbtItem.setInteger("custompickaxexp", 0);
        nbtItem.setInteger("custompickaxelevel", 1);
        nbtItem.applyNBT(itemStack);

        return itemStack;
    }

    private ItemStack update(ItemStack itemStack) {
        itemStack.setType(getMaterial());
        itemStack.setItemMeta(getItem().getItemMeta());

        updateLore(itemStack);

        return itemStack;
    }

    public void addRandomVanilla() {
        if (VANILLA_ENCHANTS.isEmpty()) return;

        Random random = new Random();
        List<Enchantment> availableEnchants = new ArrayList<>(VANILLA_ENCHANTS);
        Collections.shuffle(availableEnchants);

        for (Enchantment enchantment : availableEnchants) {
            Set<Enchantment> addedEnchants = enchantments.keySet();

            if (addedEnchants.contains(Enchantment.SILK_TOUCH) && enchantment.equals(Enchantment.LOOT_BONUS_BLOCKS)) continue;
            if (addedEnchants.contains(Enchantment.LOOT_BONUS_BLOCKS) && enchantment.equals(Enchantment.SILK_TOUCH)) continue;

            if (enchantments.containsKey(enchantment) && enchantments.get(enchantment) >= MAX_LEVEL_ENCHANTS) continue;

            if (enchantment.equals(Enchantment.SILK_TOUCH)) {
                int randomChance = random.nextInt(100) + 1;
                if (randomChance > SILK_MAX_CHANCE) continue;
            }

            if (enchantment.equals(Enchantment.SILK_TOUCH) && addedEnchants.contains(enchantment)) continue;

            int enchantmentLevel = enchantments.getOrDefault(enchantment, 0) + 1;
            enchantments.put(enchantment, enchantmentLevel);
            break;
        }
    }



    public void addRandomEnchant() {
        if (CUSTOM_ENCHANTS.isEmpty()) return;

        int randomChance = new Random().nextInt(100) + 1;
        if (randomChance <= CUSTOM_ENCHANT_CHANCE) {
            int size = CUSTOM_ENCHANTS.size();
            CustomEnchant enchant = CUSTOM_ENCHANTS.get(new Random().nextInt(size));

            Set<Enchantment> customEnchantSet = enchantments.keySet();
            if (customEnchantSet.containsAll(CUSTOM_ENCHANTS)) return;


            if (enchantments.containsKey(enchant)) {
                addRandomEnchant();
                return;
            }

            enchantments.put(enchant, 1);

        }
    }

    public boolean addXp(int xpToAdd, ItemStack stack) {
        if (level >= MAX_LEVEL) {
            return false;
        }

        this.xp += xpToAdd;
        boolean leveledUp = false;

        while (xp >= getXpNextLevel()) {
            xp -= getXpNextLevel();
            level++;
            leveledUp = true;


            if (level % LEVEL_VANILLA_ENCHANT == 0) {
                addRandomVanilla();
            }

            if (level >= CUSTOM_LEVEL) {
                addRandomEnchant();
            }
            if (level >= MAX_LEVEL) {
                xp = 0;
                break;
            }
        }
        updateLore(stack);
        return leveledUp;
    }

    private int lastTier() {
        return (MAX_LEVEL / UP_TIER);
    }

    public boolean canUpTier() {
        if (getTier() >= lastTier()) return false;
        return level % UP_TIER == 0;
    }

    public boolean isMaxLevel() { return level == MAX_LEVEL; }

    public void upTier(ItemStack itemStack) {
        if (!(canUpTier())) return;
        if (PickaxeType.getMaterialByTier(getTier()) != type.material) {
            this.type = PickaxeType.getTypeByLevel(level);
            update(itemStack);
        }
    }

    public int getTier() {
        return (level / UP_TIER);
    }

    public static boolean hasSkin(ItemStack itemStack) {
        if (!(itemStack.hasItemMeta() || itemStack.getItemMeta().hasLore())) return false;
        return SkinType.getSkinByLore(itemStack.getItemMeta().getLore()) != SkinType.VANILLA;
    }

    public int getXpNextLevel() {
        if (level >= MAX_LEVEL) return 0;
        return level * LEVEL;
    }

    private String getBars() {
        int barsCount = 38;  // Total de barras
        int completedBars = (int) Math.ceil((double) xp / getXpNextLevel() * barsCount);
        StringBuilder bars = new StringBuilder();

        if (level >= MAX_LEVEL) {
            for (int i = 0; i < barsCount; i++) {
                bars.append("§a|");
            }
        } else {
            for (int i = 0; i < completedBars; i++) {
                bars.append("§a|");
            }

            for (int i = completedBars; i < barsCount; i++) {
                bars.append("§c|");
            }
        }

        return bars.toString();
    }

    private Material getMaterial() {
        return PickaxeType.getMaterialByTier(getTier());
    }

    private static List<Enchantment> getEnchantVanilaAvaliables() {
        List<Enchantment> enchantments = new ArrayList<>();
        for (Enchantment enchant : Enchantment.values()) {
            if (enchant.getItemTarget()  == EnchantmentTarget.TOOL && enchant != Enchantment.DURABILITY && !(enchant instanceof CustomEnchant)) {
                enchantments.add(enchant);
            }
        }
        return enchantments;
    }

    private static List<CustomEnchant> getCustomEnchantments() {
        return new ArrayList<>(Manager.getEnchants());
    }

    public void updateLore(ItemStack itemStack) {
        ItemMeta builder = itemStack.getItemMeta();
        builder.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        if (builder.hasLore()) builder.getLore().clear();

        List<String> lore = new ArrayList<>();

        lore.add((level < MAX_LEVEL) ? "§7Level: §f" + level : "§7Level: §e" + level);
        lore.add((level < MAX_LEVEL) ? "§7XP §f" + xp + "§f/§f" + getXpNextLevel() : "§7XP: §eMAX");
        lore.add("§8[" + getBars() + "§8]");
        if (CustomEnchant.hasCustomEnchant(itemStack)) lore.add(" ");

        for (Enchantment enchant : enchantments.keySet()) {
            if (enchant instanceof CustomEnchant) {
                CustomEnchant customEnchant = (CustomEnchant) enchant;
                builder.addEnchant(customEnchant, 1, true);
                lore.add(customEnchant.lore(itemStack)); continue;
            }
            builder.addEnchant(enchant, enchantments.getOrDefault(enchant, 1), true);
        }

        //Remover condição para lore aparecer da skin padrão
        if (skinType != SkinType.DEFAULT) {
            lore.add("");
            lore.add(skinType.getLore());
        }

        builder.setLore(lore);
        itemStack.setItemMeta(builder);
        updateNBT(itemStack);
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(type.getPermission());
    }

    private void updateNBT(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("custompickaxexp", xp);
        nbtItem.setInteger("custompickaxelevel", level);
        nbtItem.applyNBT(itemStack);
    }

}
