package com.daniel.indotools;

import com.daniel.indotools.commands.Teste;
import com.daniel.indotools.handler.EnchantmentHandler;
import com.daniel.indotools.listeners.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        EnchantmentHandler.register();

        register();
    }

    private void register() {
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);

        getCommand("teste").setExecutor(new Teste());
    }

    public static Main getInstance() {
        return Main.getPlugin(Main.class);
    }
}