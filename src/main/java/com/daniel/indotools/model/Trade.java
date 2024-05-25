package com.daniel.indotools.model;

import com.daniel.indotools.Main;
import com.daniel.indotools.api.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class Trade implements InventoryHolder, Listener {

    private enum TradeStatus {

        WAITING, SELECTING, FINISHING;

    }

    @Setter
    private Player target;
    private Player player;

    private Inventory inventory;
    private Map<Player, ItemStack> tradeItems;

    private TradeStatus status;
    private Map<Player, Boolean> readyStatus;
    private List<Player> moneyOption;

    public Trade(Player player) {
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 3*9, "Trade");
        this.status = TradeStatus.WAITING;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Trade)) return;

        Trade trade = (Trade) e.getInventory().getHolder();

        if (!(trade == this)) return;
        e.setCancelled(true);

        Player clicker = (Player) e.getWhoClicked();

        ItemStack item = e.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) return;

        if (e.getClickedInventory().getType() != InventoryType.PLAYER) {

            if (e.getSlot() == 14 || e.getSlot() == 12) {

                if (e.getSlot() == 14 && clicker.equals(player)) return;
                if (e.getSlot() == 12 && clicker.equals(target)) return;

                if (tradeItems.containsKey(clicker)) {
                    e.getInventory().getItem((clicker.equals(player)) ? 12 : 14).setDurability((short) (readyStatus.get(clicker) ? 14 : 13));
                    readyStatus.replace(clicker, !readyStatus.get(clicker));
                } else {
                    clicker.sendMessage("§cVoçê precisar ter escolhido money ou ter oferecido uma picareta");
                    return;
                }

                if (allReady()) {
                    System.out.println("PRONTINHO MANE");
                    return;
                }

            }
        }

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("custompickaxeid")) return;

        if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
            if (clicker.equals(player) || clicker.equals(target)) {



                if (!moneyOption.contains(clicker)) {

                    int toSlot = (clicker.equals(player)) ? 10 : 16;

                    if (e.getInventory().getItem(toSlot) != null && e.getInventory().getItem(toSlot).getType() != Material.AIR) {

                        ItemStack toInventory = e.getInventory().getItem(toSlot);
                        e.getInventory().remove(toInventory);
                        clicker.getInventory().addItem(toInventory);
                        tradeItems.remove(clicker);
                    }

                    e.getInventory().setItem(toSlot, item);
                    clicker.getInventory().remove(item);
                    tradeItems.put(clicker, item);
                    clicker.playSound(clicker.getLocation(), Sound.CLICK, 1f, 1f);
                } else {
                    clicker.sendMessage("§cOpção de money ja escolhida.");
                }
            }
        }
    }

    public void setItens(Inventory inventory) {

        ItemStack done = new ItemBuilder(Material.STAINED_CLAY, (short) 14).setDisplayName("§f ").build();

        fill(inventory);
        inventory.setItem(12, done);
        inventory.setItem(14, done);

    }


    public void start() {
        if (target == null) return;
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());

        this.status = TradeStatus.SELECTING;
        this.readyStatus = new HashMap<>();
        this.tradeItems = new HashMap<>();
        this.moneyOption = new ArrayList<>();

        this.readyStatus.put(player, false);
        this.readyStatus.put(target, false);

        player.openInventory(getInventory());
        target.openInventory(getInventory());
    }

    public void reset() {
        this.status = TradeStatus.WAITING;

        tradeItems.clear();
        readyStatus.clear();

        HandlerList.unregisterAll(this);

    }

    protected void fill(Inventory inventory) {
        ItemStack cyan = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 9)
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .setName("&f ")
                .build();

        ItemStack black = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15)
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .setDisplayName("§f ")
                .build();

        int[] cyanSlots = {1, 7, 9, 17, 19, 25};

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) continue;

            boolean isCyan = false;
            for (int slot : cyanSlots) {
                if (slot == i) {
                    isCyan = true;
                    break;
                }
            }

            if (isCyan) {
                inventory.setItem(i, cyan);
            } else if (i != 10 && i != 16) {
                inventory.setItem(i, black);
            }
        }
    }

    private boolean allReady() {
        return !readyStatus.containsValue(false);
    }

    @Override
    public Inventory getInventory() {
        setItens(inventory);
        return inventory;
    }
}
