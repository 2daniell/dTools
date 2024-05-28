package com.daniel.indotools.storage;

import com.daniel.indotools.Main;
import com.daniel.indotools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cache {

    public static Connection connect() {
        try {
            File dataFolder = new File(Main.getInstance().getDataFolder() + "/database");
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            File file = new File(dataFolder, "database.db");

            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (SQLException | ClassNotFoundException e) {
            Main.getInstance().getLogger().warning("Conexão com database falhou!");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
        return null;
    }

    public static void createTable() {
        try(Connection con = connect()) {

            String query = "CREATE TABLE IF NOT EXISTS treasure_items (name VARCHAR(50) PRIMARY KEY, item TEXT)";

            PreparedStatement stm = con.prepareStatement(query);
            stm.executeUpdate();

        } catch (SQLException e) {
            Main.getInstance().getLogger().warning("Conexão com database falhou!");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            throw new RuntimeException(e);
        }
    }

    public static void addItem(String name, String item) {
        try(Connection con = connect()) {

            String query = "INSERT INTO treasure_items (name, item) VALUES (?, ?)";

            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, name);
            stm.setString(2, item);
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteItem(String name) {
        try(Connection con = connect()) {

            String query = "DELETE FROM treasure_items WHERE name = ?";

            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, name);
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String key) {
        try (Connection con = connect()) {
            String query = "SELECT COUNT(*) FROM treasure_items WHERE name = ?";

            try (PreparedStatement stm = con.prepareStatement(query)) {
                stm.setString(1, key);
                try (ResultSet rs = stm.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        return count > 0;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar a existência do registro no banco de dados.", e);
        }
        return false;
    }

    public static List<ItemStack> load() {

        List<ItemStack> items = new ArrayList<>();
        try (Connection con = connect()) {

            String query = "SELECT * FROM treasure_items";

            PreparedStatement stm = con.prepareStatement(query);

            ResultSet rs = stm.executeQuery();

            while(rs.next()) {

                ItemStack[] itemStacks = Utils.read(rs.getString("item"));

                items.addAll(Arrays.asList(itemStacks));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }


}
