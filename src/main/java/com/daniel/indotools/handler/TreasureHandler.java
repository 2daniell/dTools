package com.daniel.indotools.handler;

import com.daniel.indotools.storage.Cache;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class TreasureHandler {

    private static List<ItemStack> items;

    public static void load() {
        items = Cache.load();
    }

    public static ItemStack getRandom() {
        return items.get(new Random().nextInt(items.size()));
    }

    public static List<ItemStack> getItems() {
        return items;
    }
}
