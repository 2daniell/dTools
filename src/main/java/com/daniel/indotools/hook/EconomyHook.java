package com.daniel.indotools.hook;

import com.daniel.indotools.Main;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class EconomyHook {

    @Getter
    private static Economy economy;

    public static void setupEconomy() {
        final RegisteredServiceProvider<Economy> registration = Bukkit.getServicesManager().getRegistration(Economy.class);

        if(registration == null) {
            Main.getInstance().getLogger().warning("Nenhuma dependência de economia encontrada, desabilitando o plugin.");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        } else {
            economy = registration.getProvider();
        }
    }

    public static void depositCoins(OfflinePlayer player, double amount) {
        economy.depositPlayer(player, amount);
    }

    public static double getBalanceOf(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public static void removeCoins(OfflinePlayer player, double coins) {
        economy.withdrawPlayer(player, coins);
    }
}
