package com.dolphln.spigotcams.listener;

import com.dolphln.spigotcams.SpigotCams;
import com.dolphln.spigotcams.models.CamInfo;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CamListener implements Listener {

    private final SpigotCams plugin;

    public CamListener(SpigotCams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChangeGamemode(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();
        if (e.getNewGameMode() == GameMode.SPECTATOR) return;
        if (this.plugin.getCameraManager().isPlayerInCam(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDie(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        if (player.getHealth() - e.getFinalDamage() <= 0.0D && this.plugin.getCameraManager().isPlayerInCam(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (this.plugin.getCameraManager().isPlayerInCam(player)) {
            this.plugin.getCameraManager().removePlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent e) {
        this.processMoveEvent(e);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerTeleportEvent e) {
        this.processMoveEvent(e);
    }

    private void processMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        CamInfo camInfo = this.plugin.getCameraManager().getPlayerCam(player);

        if (camInfo == null) return;
        e.setTo(camInfo.getLocation().getLocation());
    }
}
