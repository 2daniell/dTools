package com.daniel.indotools.storage;

import com.daniel.indotools.model.Pickaxe;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class Cache<T> extends ArrayList<T> {

    public Optional<T> find(Predicate<T> predicate) {
        return stream().filter(predicate).findFirst();
    }

    /*public void persist() {
        for (T t : this) {
            if (t instanceof Pickaxe) {

                Pickaxe pickaxe = (Pickaxe) t;

                Player player = Bukkit.getPlayer(pickaxe.getOwner());
                if (player == null || !player.isOnline()) continue;

                for (ItemStack itemStack : player.getInventory().getContents()) {

                    NBTItem nbtItem = new NBTItem(itemStack);
                    if (!nbtItem.hasTag("custompickaxeid")) continue;

                    UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));
                    if (!pickaxe.getId().equals(id)) continue;



                    break;
                }

            }
        }
    }*/
}
