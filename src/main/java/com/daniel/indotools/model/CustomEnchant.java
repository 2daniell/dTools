package com.daniel.indotools.model;

import com.daniel.indotools.handler.Manager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.inventory.ItemStack;
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


    public void register(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /*public static boolean hasCustomEnchant(ItemStack stack) {
        return stack.getEnchantments().containsKey();
    }*/

    protected abstract int getEnchantmentLevel(BlockExpEvent event);

    protected abstract String lore();

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
