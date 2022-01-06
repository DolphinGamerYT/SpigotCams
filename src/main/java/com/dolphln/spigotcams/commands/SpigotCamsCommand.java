package com.dolphln.spigotcams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.dolphln.spigotcams.SpigotCams;
import com.dolphln.spigotcams.gui.CamMenu;
import com.dolphln.spigotcams.models.BasicLocation;
import com.dolphln.spigotcams.models.CamInfo;
import com.dolphln.spigotcams.models.CamType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("spigotcams|cams")
@CommandPermission("spigotcams.command")
public class SpigotCamsCommand extends BaseCommand {

    private final SpigotCams plugin;

    public SpigotCamsCommand(SpigotCams plugin) {
        this.plugin = plugin;
    }

    @Subcommand("menu")
    @Default
    public void onMenu(Player player) {
        new CamMenu(player);
    }

    @Subcommand("add")
    @CommandCompletion("@nothing")
    @Syntax("[cameraName]")
    public void onAdd(Player player, @Single @Optional @Default("Unknown") String name) {
        CamInfo cam = new CamInfo(UUID.randomUUID(), name, new BasicLocation(player.getLocation()), CamType.NORMAL);
        plugin.getCameraManager().addCam(cam);
        player.sendMessage("Added camera " + cam.getName());
    }

    @Subcommand("remove")
    @CommandCompletion("@cameras")
    @Syntax("<cameraUUID>")
    public void onRemove(Player player, @Single String uuid) {
        UUID cameraUUID;
        try {
            cameraUUID = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid UUID");
            return;
        }

        CamInfo cam = plugin.getCameraManager().getCam(cameraUUID);
        if (cam == null) {
            player.sendMessage(ChatColor.RED + "Camera not found");
            return;
        }
        plugin.getCameraManager().removeCam(cam);
        player.sendMessage(ChatColor.GREEN + "Camera removed");
    }

    @Subcommand("goto")
    @CommandCompletion("@cameras")
    @Syntax("<cameraUUID>")
    public void onGoto(Player player, @Single String uuid) {
        UUID cameraUUID;
        try {
            cameraUUID = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid UUID");
            return;
        }

        CamInfo cam = plugin.getCameraManager().getCam(cameraUUID);
        if (cam == null) {
            player.sendMessage(ChatColor.RED + "Camera not found");
            return;
        }

        player.teleport(cam.getLocation().getLocation());
        player.sendMessage(ChatColor.GREEN + "Teleported to camera " + cam.getName());
    }

    @Subcommand("getname")
    @CommandCompletion("@cameras")
    @Syntax("<cameraUUID>")
    public void onGetName(Player player, @Single String uuid) {
        UUID cameraUUID;
        try {
            cameraUUID = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid UUID");
            return;
        }

        CamInfo cam = plugin.getCameraManager().getCam(cameraUUID);
        if (cam == null) {
            player.sendMessage(ChatColor.RED + "Camera not found");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Camera with UUID " + cam.getUuid().toString() + " is called " + cam.getName());
    }

    @Subcommand("set")
    @CommandCompletion("@cameras @players")
    @Syntax("<cameraUUID> [playerName]")
    public void onSet(Player player, String uuid, @Optional String playerName) {
        CamInfo cam = plugin.getCameraManager().getCam(UUID.fromString(uuid));
        if (cam == null) {
            player.sendMessage(ChatColor.RED + "Camera not found");
            return;
        }

        if (playerName != null) {
            Player target = plugin.getServer().getPlayer(playerName);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return;
            }
            this.plugin.getCameraManager().addPlayer(target, cam);
            player.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " added to camera " + cam.getName());
            target.sendMessage(ChatColor.GREEN + "You have been added to camera " + cam.getName() + " by " + player.getName());
            return;
        }
        this.plugin.getCameraManager().addPlayer(player, cam);
        player.sendMessage(ChatColor.GREEN + "You have been added to camera " + cam.getName());
    }

    @Subcommand("leave")
    @CommandCompletion("@players")
    @Syntax("[playerName]")
    public void onLeave(Player player, @Optional String playerName) {
        if (playerName == null) {
            this.plugin.getCameraManager().removePlayer(player);
            player.sendMessage(ChatColor.GREEN + "You have been removed from all cameras");
            return;
        }
        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Player not found");
            return;
        }
        this.plugin.getCameraManager().removePlayer(target);
        player.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " removed from all cameras");
        target.sendMessage(ChatColor.GREEN + "You have been removed from all cameras");
    }



}
