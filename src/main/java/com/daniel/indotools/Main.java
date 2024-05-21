package com.daniel.indotools;

import com.daniel.indotools.commands.Teste;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.listeners.Listeners;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    private final static Map<String, FileConfiguration> configs = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Manager.register();

        register();
    }

    private void register() {
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);

        getCommand("teste").setExecutor(new Teste());
    }

    public static FileConfiguration config() {
        return getInstance().getConfig();
    }

    public static Main getInstance() {
        return Main.getPlugin(Main.class);
    }
}