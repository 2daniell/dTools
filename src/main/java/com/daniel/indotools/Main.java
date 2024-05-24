package com.daniel.indotools;

import com.daniel.indotools.commands.PickaxeCommand;
import com.daniel.indotools.commands.TesouroCommand;
import com.daniel.indotools.handler.Manager;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.handler.TreasureHandler;
import com.daniel.indotools.hook.EconomyHook;
import com.daniel.indotools.listeners.Listeners;
import com.daniel.indotools.storage.Cache;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        register();

        EconomyHook.setupEconomy();
        Cache.createTable();

        TreasureHandler.load();

        Bukkit.getScheduler().runTask(this, Manager::register);
    }

    @Override
    public void onDisable() {
        Manager.unRegister();
    }

    private void register() {
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);

        getCommand("picareta").setExecutor(new PickaxeCommand());
        getCommand("itemtesouro").setExecutor(new TesouroCommand());
    }

    public static FileConfiguration config() {
        return getInstance().getConfig();
    }

    public static Main getInstance() {
        return Main.getPlugin(Main.class);
    }
}