package com.daniel.indotools.handler;

import com.daniel.indotools.Main;
import com.daniel.indotools.enchants.Explosion;
import com.daniel.indotools.model.CustomEnchant;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EnchantmentHandler {

    private static final Set<CustomEnchant> enchants = new HashSet<>();

    static {
        enchants.add(new Explosion());
    }

    public static CustomEnchant findByName(String name) {
        return search(e -> e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private static Stream<CustomEnchant> search(Predicate<CustomEnchant> predicate) {
        return enchants.stream().filter(predicate);
    }

    public static void register() {
        enchants.forEach(enchant -> {
            enchant.register(Main.getInstance());
            registerCustomEnchantment(enchant);
        });
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
}
