package com.daniel.indotools.model;

import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.enchants.DoubleDrop;
import com.daniel.indotools.enchants.DoubleXP;
import com.daniel.indotools.enchants.Explosion;
import com.daniel.indotools.enchants.Treasure;
import com.daniel.indotools.handler.Manager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class CustomEnchant extends Enchantment implements Listener {

    private final Map<Class<? extends BlockExpEvent>, BiConsumer<BlockExpEvent, Integer>> eventHandles = new HashMap<>();

    private final String name;
    private final int maxLevel, startLevel;

    public CustomEnchant(String name, int id, int maxLevel, int startLevel) {
        super(id);
        this.name = name;
        this.maxLevel = maxLevel;
        this.startLevel = startLevel;
    }

    @EventHandler
    public void onEvent(BlockExpEvent e) {
        if (!Manager.isWorld(e.getBlock().getLocation().getWorld().getName())) return;
        if (eventHandles.containsKey(e.getClass())) {
            int level = getEnchantmentLevel(e);
            eventHandles.get(e.getClass()).accept(e, level);
        }
    }

    public <T extends BlockExpEvent> void add(Class<T> eventType, BiConsumer<T, Integer> handler) {
        eventHandles.put(eventType, (BiConsumer<BlockExpEvent, Integer>) handler);
    }

    public abstract ItemStack getBook();

    public static ItemStack getAllBook() {
        ItemStack book = new ItemBuilder(Material.ENCHANTED_BOOK)
                .setName("      &7-== &bEncantamentos Customizados &7==-")
                .setLore("",
                        "§bEXPLOSIVE 5%",
                        "§b2X DROP 5%",
                        "§b2X XP 5%",
                        "§bTESOURO 5%",
                        "",
                        "§7Esse item serve para setar todos",
                        "§7os encantamentos customizados",
                        "§7em uma §ePicareta §8[§6Tier V§8]§7.",
                        "",
                        "§7Para usar o item clique com o §aBotão Direito §7sobre o item desejado!").build();
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(new Treasure(), 1, true);
        meta.addStoredEnchant(new Explosion(), 1, true);
        meta.addStoredEnchant(new DoubleDrop(), 1, true);
        meta.addStoredEnchant(new DoubleXP(), 1, true);
        book.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(book);
        nbtItem.setBoolean("allcustomenchant", true);
        nbtItem.applyNBT(book);
        return book;
    }

    public void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static boolean hasCustomEnchant(ItemStack stack) {
        return stack.getEnchantments().keySet().stream().anyMatch(enchantment -> enchantment instanceof CustomEnchant);
    }

    protected abstract int getEnchantmentLevel(BlockExpEvent event);

    protected abstract String lore(ItemStack itemStack);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getStartLevel() {
        return startLevel;
    }

}
