package com.dolphln.spigotcams.gui;

import com.dolphln.spigotcams.SpigotCams;
import com.dolphln.spigotcams.models.CamInfo;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CamMenu {

    private final SpigotCams plugin;
    private final Player player;
    private final CamInfo playerCam;

    private final ChestGui gui;
    private final String titleTemplate;

    private PaginatedPane camsPane;

    public CamMenu(Player player) {
        this.plugin = SpigotCams.getInstance();
        this.player = player;
        this.playerCam = plugin.getCameraManager().getPlayerCam(player);

        this.gui = new ChestGui(6, ".");
        this.titleTemplate = "&2(%p%/%m%) &8&lCamera Menu";

        this.buildGui();
        this.updateTitle();
        this.plugin.getCameraManager().removePlayer(player);
        this.gui.show(player);
    }

    private void buildGui() {
        this.gui.setOnTopDrag(e -> e.setCancelled(true));
        this.gui.setOnTopDrag(e -> e.setCancelled(true));

        ItemStack bgItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bgItem.getItemMeta();
        bgMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f "));
        bgMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        bgItem.setItemMeta(bgMeta);
        StaticPane bg = new StaticPane(0, 5, 9, 1, Pane.Priority.LOWEST);
        bg.fillWith(bgItem);
        bg.setOnClick(e -> e.setCancelled(true));
        this.gui.addPane(bg);

        this.camsPane = getCamPages();
        this.gui.addPane(this.camsPane);

        StaticPane navigation = this.getNavigation();
        this.gui.addPane(navigation);
    }

    private PaginatedPane getCamPages() {
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);

        pane.populateWithGuiItems(
                this.plugin.getCameraManager().getCams().stream().map(cam -> new GuiItem(this.getCamItem(cam), e -> {
                    e.setCancelled(true);
                    this.plugin.getCameraManager().addPlayer(player, cam);
                    player.closeInventory();
                })).collect(Collectors.toList())
        );

        return pane;
    }

    private StaticPane getNavigation() {
        StaticPane navigation = new StaticPane(0, 5, 9, 1, Pane.Priority.NORMAL);

        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aNext Page"));
        nextPageMeta.setLore(Stream.of("&7Click to go to the next page").map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));
        nextPage.setItemMeta(nextPageMeta);
        navigation.addItem(new GuiItem(nextPage, event -> {
            event.setCancelled(true);
            if (this.camsPane.getPage() < this.camsPane.getPages() - 1) {
                this.camsPane.setPage(this.camsPane.getPage() + 1);

                this.updateTitle();
                gui.update();
            }
        }), 8, 0);

        ItemStack prevPage = new ItemStack(Material.ARROW);
        ItemMeta prevPageMeta = prevPage.getItemMeta();
        prevPageMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPrevious Page"));
        prevPageMeta.setLore(Stream.of("&7Click to go to the previous page").map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));
        prevPage.setItemMeta(prevPageMeta);
        navigation.addItem(new GuiItem(prevPage, event -> {
            event.setCancelled(true);
            if (this.camsPane.getPage() > 0) {
                this.camsPane.setPage(this.camsPane.getPage() - 1);

                this.updateTitle();
                gui.update();
            }
        }), 0, 0);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cClose"));
        closeMeta.setLore(Stream.of("&7Click to close this menu").map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));
        close.setItemMeta(closeMeta);
        navigation.addItem(new GuiItem(close, event -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }), 4, 0);

        /*if (this.playerCam != null) {
            ItemStack exitCam = new ItemStack(Material.FEATHER);
            ItemMeta exitCamMeta = exitCam.getItemMeta();
            exitCamMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Exit Camera"));
            exitCamMeta.setLore(Stream.of("&7Click to exit the camera you're on").map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));
            exitCamMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            exitCamMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            exitCam.setItemMeta(exitCamMeta);
            navigation.addItem(new GuiItem(exitCam, event -> {
                event.setCancelled(true);
                plugin.getCameraManager().removePlayer(player);
                player.closeInventory();
            }), 3, 0);
        }*/

        return navigation;
    }

    private ItemStack getCamItem(CamInfo cam) {
        //boolean playerInThisCam = this.playerCam != null && this.playerCam.getUuid().equals(cam.getUuid());
        ItemStack item = new ItemStack(Material.ARMOR_STAND);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + cam.getName()));

        //List<String> lore = List.of("&8UUID: " + cam.getUuid().toString(), "&f ", playerInThisCam ? "&cYou are on this camera" : "&7Click to view this camera");
        List<String> lore = List.of("&8UUID: " + cam.getUuid().toString(), "&f ", "&7Click to view this camera");
        meta.setLore(lore.stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList()));

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        /*if (playerInThisCam) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
        }*/

        item.setItemMeta(meta);
        return item;
    }

    private void updateTitle() {
        this.gui.setTitle(
                ChatColor.translateAlternateColorCodes('&',
                this.titleTemplate.replace("%p%", String.valueOf(this.camsPane.getPage()+1)).replace("%m%", String.valueOf(Math.max(this.camsPane.getPages(), 1)))
            ));
    }


}
