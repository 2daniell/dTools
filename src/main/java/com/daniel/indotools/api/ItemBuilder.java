package com.daniel.indotools.api;

import com.daniel.indotools.Main;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private ItemStack stack;

    public ItemBuilder(Material mat) {
        stack = new ItemStack(mat);
    }

    public ItemBuilder(Material mat, short sh) {
        stack = new ItemStack(mat, 1, sh);
    }

    public ItemBuilder(ItemStack stack, int amount) {
        stack.setAmount(amount);
        this.stack = stack;
    }

    public ItemBuilder(String texture) {
        ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skullItem.getItemMeta();
        if (texture == null || texture.isEmpty()) {
            stack = skullItem;
            return;

        }
        if (!texture.startsWith("http://textures.minecraft.net/texture/"))
            texture = "http://textures.minecraft.net/texture/" + texture;

        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), null);

        profile.getProperties().put("textures", new Property("textures", new String(Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture).getBytes()))));
        Field field = null;
        try {
            field = skullMeta.getClass().getDeclaredField("profile");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(field).setAccessible(true);
        try {
            field.set(skullMeta, profile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        skullItem.setItemMeta(meta);
        this.stack = skullItem;
    }

    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder(ItemStack stack) {
        this.stack = stack;
    }

    public ItemBuilder setGlow (boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }

        return this;
    }

    public ItemBuilder setUnbreakable() {
        ItemMeta meta = stack.getItemMeta();
        meta.spigot().setUnbreakable(true);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setBannerColor (DyeColor color) {
        BannerMeta meta = (BannerMeta) stack.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder addLore(String lore) {
        stack.getItemMeta().getLore().add(lore);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

   /* @SuppressWarnings("deprecation")
    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        if (Main.getVersion().value >= 13) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
        } else {
            meta.setOwner(owner);
        }
        setItemMeta(meta);
        return this;
    }*/

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(displayname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setName(String displayname) {
        setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
        return this;
    }

    public ItemBuilder setItemStack (ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeFlags(ItemFlag... flags) {
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.removeItemFlags(flags);
        return this;
    }

    public ItemBuilder setLore (String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        ItemMeta meta = getItemMeta();
        if (lore != null && lore.length > 0) {
            List<String> coloredLore = Arrays.stream(lore)
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList());
            meta.setLore(coloredLore);
        }
        setItemMeta(meta);
        return this;
    }


    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag flag) {
        addItemFlag(flag);
        return this;
    }

    public ItemStack build() {
        return stack;
    }

}
