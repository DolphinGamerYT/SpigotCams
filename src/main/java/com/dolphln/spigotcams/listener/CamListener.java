package com.dolphln.spigotcams.listener;

import com.dolphln.spigotcams.SpigotCams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CamListener implements Listener {

    private final SpigotCams plugin;

    public CamListener(SpigotCams plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChangeGamemode(PlayerGameModeChangeEvent e) {
        Player player = e.getPlayer();

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
}
