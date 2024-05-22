package com.daniel.indotools.inventories;

import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PickaxeInventory extends Menu {

    private static final int PRICE = 20000;

    public PickaxeInventory(Player player) {
        super(player, "   §7-== §bI§7n§bd§7o§bm§7i§bn§7u§bs §7N§be§7t§bw§7o§br§7k §7==-", 3*9);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("")) {

        }
    }

    @Override
    public void setItens(Inventory inventory) {
        ItemStack X = new ItemBuilder("http://textures.minecraft.net/texture/ed0a1420844ce237a45d2e7e544d135841e9f82d09e203267cf8896c8515e360").setName("§cFechar").build();
        ItemStack pickaxe = new ItemBuilder(Material.WOOD_PICKAXE).setName("§fComprar Picareta").setLore("","§7Custo: §a$ §f" + PRICE,"", "§7Click Aqui para pegar uma picareta").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).build();
        ItemStack info = new ItemBuilder(Material.NETHER_STAR).setName("§fInformações").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).build();

        fill(inventory);
        inventory.setItem(22,X);
        inventory.setItem(11,pickaxe);
        inventory.setItem(4,info);
    }

    private void fill(Inventory inventory) {
        int slots = 0;
        for (ItemStack items : inventory.getContents()) {
            if (items != null && items.getType() != Material.AIR) continue;
            inventory.setItem(slots++, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§f ").build());
        }
    }
}
