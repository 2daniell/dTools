package com.daniel.indotools.handler;

import com.daniel.indotools.Main;
import com.daniel.indotools.model.Skin;
import com.daniel.indotools.objects.enums.SkinType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkinHandler {

    private final static Set<Skin> skins = new HashSet<>();

    private static Connection connect() {
        if (Main.config().getBoolean("mysql.use")) {

            String host = Main.config().getString("mysql.host");
            String port = Main.config().getString("mysql.port");
            String database = Main.config().getString("mysql.database");
            String password = Main.config().getString("mysql.password");
            String username = Main.config().getString("mysql.username");

            String url = "jdbc:mysql://"+host+":"+port+"/"+database+"?useUnicode=true&characterEncoding=utf8";

            try {
                return DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                Main.getInstance().getLogger().warning("Conexão com database falhou! Alterando para SQLite");
            }

        }

        File dataFolder = new File(Main.getInstance().getDataFolder() + "/database");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File file = new File(dataFolder, "skins.db");
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (ClassNotFoundException | SQLException e) {
            Main.getInstance().getLogger().warning("Conexão com database falhou!");
            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
        return null;
    }

    public static void createTable() {
        try (Connection con = connect()) {

            String query = "CREATE TABLE IF NOT EXISTS players_skins (id TEXT, player TEXT, skin TEXT)";

            PreparedStatement stm = con.prepareStatement(query);
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void persist(Skin skin) {
        try(Connection con = connect()) {

            String query = "INSERT INTO players_skins(id, player, skin) VALUES (?,?,?)";

            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, skin.getId().toString());
            stm.setString(2, skin.getOwner().toString());
            stm.setString(3, skin.getSkinType().toString());
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasSkin(Player player, SkinType type) {
        return findSkinsPlayer(player).stream().anyMatch(e -> e.getSkinType() == type);
    }

    public static void load() {
        try(Connection con = connect()) {

            String query = "SELECT * FROM players_skins";
            PreparedStatement stm = con.prepareStatement(query);

            ResultSet rs = stm.executeQuery();
            while(rs.next()) {

                UUID id = UUID.fromString(rs.getString("id"));
                UUID player = UUID.fromString(rs.getString("player"));
                SkinType skin = SkinType.valueOf(rs.getString("skin"));

                Skin skinsPlayer = new Skin(id, player, skin);
                skins.add(skinsPlayer);

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void remove(Skin skin) {
        try(Connection con = connect()) {

            String query = "DELETE FROM players_skins WHERE id = ?";
            PreparedStatement stm = con.prepareStatement(query);
            stm.setString(1, skin.getId().toString());
            stm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Skin> findSkinsPlayer(Player player) {
        return find(e -> e.getOwner().equals(player.getUniqueId())).collect(Collectors.toList());
    }

    public static boolean addSkin(Skin skin) {
        persist(skin);
        return skins.add(skin);
    }

    public static boolean removeSkin(Skin skin) {
        remove(skin);
        return skins.remove(skin);
    }

    private static Optional<Skin> search(Predicate<Skin> predicate) {
        return skins.stream().filter(predicate).findFirst();
    }

    private static Stream<Skin> find(Predicate<Skin> predicate) {
        return skins.stream().filter(predicate);
    }

    public static Set<Skin> getSkins() {
        return skins;
    }
}
