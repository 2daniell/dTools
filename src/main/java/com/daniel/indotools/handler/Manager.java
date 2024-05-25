package com.daniel.indotools.handler;

import com.daniel.indotools.Main;
import com.daniel.indotools.enchants.DoubleDrop;
import com.daniel.indotools.enchants.DoubleXP;
import com.daniel.indotools.enchants.Explosion;
import com.daniel.indotools.enchants.Treasure;
import com.daniel.indotools.model.CustomEnchant;
import com.daniel.indotools.objects.Pair;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Manager {

    private static final Set<CustomEnchant> enchants = new HashSet<>();
    private static final Set<String> worldsMina = new HashSet<>();
    private static final Map<Material, Integer> blocks = new HashMap<>();

    static {
        enchants.add(new Explosion());
        enchants.add(new Treasure());
        enchants.add(new DoubleXP());
        enchants.add(new DoubleDrop());
        loadMinas();
        loadBlocks();
    }

    public static void register() {
        enchants.forEach(enchant -> {
            enchant.register(Main.getInstance());
            registerCustomEnchantment(enchant);
        });
    }

    public static void unRegister() {
        enchants.forEach(Manager::unregisterCustomEnchantment);
    }

    public static boolean isWorld(String world) {
        return worldsMina.contains(world);
    }

    private static void loadMinas() {
        worldsMina.addAll(Main.config().getStringList("mina-worlds"));
    }

    private static void loadBlocks() {
        for(String key : Main.config().getConfigurationSection("blocks").getKeys(false)) {

            Material material = Material.getMaterial(Main.config().getString("blocks." + key + ".material"));
            int xp = Main.config().getInt("blocks." + key + ".xp");

            if(material == null || !material.isBlock()) {
                Main.getInstance().getLogger().warning("O material " + material + " não é valido");
                continue;
            }

            blocks.putIfAbsent(material, xp);
        }
    }

    public static int getXpBlock(Block block) {
        return blocks.getOrDefault(block.getType(), 0);
    }

    private static void registerCustomEnchantment(Enchantment enchantment) {
        try {
            Field acceptingNewField = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNewField.setAccessible(true);
            acceptingNewField.set(null, true);

            Field byIdField = Enchantment.class.getDeclaredField("byId");
            byIdField.setAccessible(true);
            Map<Integer, Enchantment> byId = (Map<Integer, Enchantment>) byIdField.get(null);

            Field byNameField = Enchantment.class.getDeclaredField("byName");
            byNameField.setAccessible(true);
            Map<String, Enchantment> byName = (Map<String, Enchantment>) byNameField.get(null);

            if (!byId.containsValue(enchantment)) {
                byId.put(enchantment.getId(), enchantment);
            }

            if (!byName.containsValue(enchantment)) {
                byName.put(enchantment.getName(), enchantment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void unregisterCustomEnchantment(Enchantment enchantment) {
        try {
            Field byIdField = Enchantment.class.getDeclaredField("byId");
            byIdField.setAccessible(true);
            Map<Integer, Enchantment> byId = (Map<Integer, Enchantment>) byIdField.get(null);

            Field byNameField = Enchantment.class.getDeclaredField("byName");
            byNameField.setAccessible(true);
            Map<String, Enchantment> byName = (Map<String, Enchantment>) byNameField.get(null);

            if (byId.containsValue(enchantment)) {
                byId.values().removeIf(customEnchantment -> customEnchantment.equals(enchantment));
            }

            if (byName.containsValue(enchantment)) {
                byName.values().removeIf(customEnchantment -> customEnchantment.equals(enchantment));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<CustomEnchant> getEnchants() {
        return enchants;
    }
}
