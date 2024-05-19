package com.daniel.indotools.utils;

public final class Utils {

    public static String getLevelColor(int level) {
        if (level >= 0 && level <= 39) return "§8[§7Tier §7§l" + level + "§8]";
        if (level >= 40 && level <= 79) return "§8[§7Tier §f§l" + level + "§8]";
        if (level > 80) return "§8[§7Tier §6§l" + level + "§8]";
        return null;
    }
}
