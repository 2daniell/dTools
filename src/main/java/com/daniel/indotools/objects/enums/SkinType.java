package com.daniel.indotools.objects.enums;

import com.daniel.indotools.api.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum SkinType {

    DEFAULT("", null),
    VANILLA("§aVanilla Skin", "§7-== §aVanila Skin §7==-"),
    MITHRIL("§bMithril Skin", "§7-== §bMithril Skin §7==-"),
    LEGENDARY("§6Legendary Skin", "§7-== §6Legendary Skin §7==-");

    private final String displayName;
    private final String lore;

    public ItemStack getIcon() {
        return new ItemBuilder(Material.DIAMOND_PICKAXE).setDisplayName(displayName)
                .addEnchant(Enchantment.KNOCKBACK, 1)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addItemFlag(ItemFlag.HIDE_ENCHANTS)
                .setLore("","§7Clique para setar a skin!","", lore).build();
    }

    public ItemStack getItem() {
        ItemStack item = new ItemBuilder(Material.DIAMOND_PICKAXE).addEnchant(Enchantment.KNOCKBACK, 1)
                .addFlags(ItemFlag.HIDE_ENCHANTS).setDisplayName(displayName).setUnbreakable().addFlags(ItemFlag.HIDE_UNBREAKABLE)
                .setLore("", "§7Ao usar o ativador, a skin ira para seu armazem de skins!", "", lore, "", "§7§nshift §7+ §7§ndireito §7para ativar").build();
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setBoolean("customskin", true);
        nbtItem.applyNBT(item);
        return item;
    }

    public static SkinType getSkinByLore(List<String> lore) {
        return Arrays.stream(values()).filter(skin -> lore.contains(skin.getLore())).findFirst().orElse(SkinType.DEFAULT);
    }

    public static SkinType getSkinByDisplayName(String displayName) {
        return Arrays.stream(values()).filter(e -> ChatColor.stripColor(e.getDisplayName()).equalsIgnoreCase(displayName)).findFirst().orElse(null);
    }
}
