package com.daniel.indotools.inventories;

import com.daniel.indotools.api.ItemBuilder;
import com.daniel.indotools.api.SkullCreator;
import com.daniel.indotools.handler.PickaxeHandler;
import com.daniel.indotools.handler.SkinHandler;
import com.daniel.indotools.menu.PaginatedMenu;
import com.daniel.indotools.model.Pickaxe;
import com.daniel.indotools.model.Skin;
import com.daniel.indotools.objects.enums.SkinType;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SkinsInventory extends PaginatedMenu {


    public SkinsInventory(Player player) {
        super(player, "        §7-== §bI§7n§bd§7o §0Skins §bN§7e§bt §7==-", 9*3, 5);
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        if (!e.getCurrentItem().hasItemMeta() || !e.getCurrentItem().getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());

        if (name.equalsIgnoreCase("        - Voltar -")) {
            player.openInventory(new PickaxeInventory(player).getInventory());
        } else if(name.equalsIgnoreCase("Anterior")) {
            page--;
            player.openInventory(getInventory());
        } else if(name.equalsIgnoreCase("Proxima")) {
            page++;
            player.openInventory(getInventory());
        }else if (name.equalsIgnoreCase("Reset")) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR || item.getType() != Material.DIAMOND_PICKAXE) continue;
                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.hasTag("custompickaxeid")) {

                    Pickaxe pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                    if (pickaxe == null) {
                        UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));
                        int xp = nbtItem.getInteger("custompickaxexp");
                        int level = nbtItem.getInteger("custompickaxelevel");
                        SkinType type = SkinType.getSkinByLore(item.getItemMeta().getLore());

                        Pickaxe pic = new Pickaxe(id, level, xp, type);
                        pic.setEnchantments(new HashMap<>(item.getEnchantments()));

                        PickaxeHandler.getPickaxes().add(pic);
                        pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                    }

                    pickaxe.setSkin(SkinType.DEFAULT, item);
                    player.sendMessage("§aSkin resetada.");
                    return;
                }
            }
            player.sendMessage("§cVocê precisa ter uma picareta no inventario.");
            return;
        } else {

            SkinType skinType = SkinType.getSkinByDisplayName(name);

            if (skinType == null) return;

            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR || item.getType() != Material.DIAMOND_PICKAXE) continue;
                NBTItem nbtItem = new NBTItem(item);
                if (nbtItem.hasTag("custompickaxeid")) {

                    Pickaxe pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                    if (pickaxe == null) {
                        UUID id = UUID.fromString(nbtItem.getString("custompickaxeid"));
                        int xp = nbtItem.getInteger("custompickaxexp");
                        int level = nbtItem.getInteger("custompickaxelevel");
                        SkinType type = SkinType.getSkinByLore(item.getItemMeta().getLore());

                        Pickaxe pic = new Pickaxe(id, level, xp, type);
                        pic.setEnchantments(new HashMap<>(item.getEnchantments()));

                        PickaxeHandler.getPickaxes().add(pic);
                        pickaxe = PickaxeHandler.findPickaxeById(UUID.fromString(nbtItem.getString("custompickaxeid")));
                    }

                    pickaxe.setSkin(skinType, item);
                    player.sendMessage("§aSkin §f" + skinType.toString() + " §asetada!");
                    return;
                }

            }
            player.sendMessage("§cVocê precisa ter uma picareta no inventario.");
            return;


        }
    }

    @Override
    public void setItens(Inventory inventory) {
        inventory.clear();
        fill(inventory);

        List<Skin> subList = subList(SkinHandler.findSkinsPlayer(player));

        ItemStack vidrobranco = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 0).addFlags(ItemFlag.HIDE_ATTRIBUTES).setName("&f ").build();

        int slot = 11;
        for (Skin skin : subList) {
            inventory.setItem(slot++, skin.getSkinType().getIcon());
        }

        for (int i = slot; i <= 15; i++) {
            inventory.setItem(i, vidrobranco);
        }

        if (subList.size() >= maxItensPerPage) inventory.setItem(17, new ItemBuilder(Material.ARROW).setDisplayName("§eProxima").build());

        if (page > 1) inventory.setItem(9, new ItemBuilder(Material.ARROW).setDisplayName("§eAnterior").build());

        inventory.setItem(22, new ItemBuilder(SkullCreator.itemFromUrl("http://textures.minecraft.net/texture/52ba81b47d5ee06b484ea9bdf22934e6abca5e4ced7be3905d6ae6ecd6fcea2a")).setName("        &7- §cVoltar &7-").setLore("","&7Clique aqui para voltar!").build());
        inventory.setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("§eReset").setLore("", "§7Clique aqui para resetar sua skin").build());

    }

    @Override
    public void fill(Inventory inventory) {
        int slots = 0;

        for (ItemStack items : inventory.getContents()) {
            if (items != null && items.getType() != Material.AIR) continue;
            inventory.setItem(slots++, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).setDisplayName("§f ").build());
        }
        int[] slotCiano = {1,9,19,7,17,25};

        for (int slot : slotCiano) {
            inventory.setItem(slot, new ItemBuilder(Material.STAINED_GLASS_PANE,(short) 9).addFlags(ItemFlag.HIDE_ATTRIBUTES).setName("&f ").build());
        }
    }
}
