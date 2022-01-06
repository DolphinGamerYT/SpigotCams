package com.dolphln.spigotcams.core;

import com.dolphln.spigotcams.SpigotCams;
import com.dolphln.spigotcams.models.BasicLocation;
import com.dolphln.spigotcams.models.CamInfo;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CameraManager {

    private final SpigotCams plugin;
    private int teleportTaskId;

    private final List<CamInfo> cams;
    private final HashMap<UUID, CamInfo> playerCameras;
    private final HashMap<UUID, BasicLocation> playerLocationsCache;
    private final HashMap<UUID, GameMode> playerGamemodesCache;

    public CameraManager(SpigotCams plugin) {
        this.plugin = plugin;

        this.cams = new ArrayList<>();
        this.playerCameras = new HashMap<>();
        this.playerLocationsCache = new HashMap<>();
        this.playerGamemodesCache = new HashMap<>();

        this.teleportTaskId = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            this.playerCameras.forEach((uuid, cam) -> {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player == null) return;

                player.teleport(cam.getLocation().getLocation());
            });
        }, 0L, 1L).getTaskId();
    }

    public void addPlayer(Player player, CamInfo cam) {
        if (player == null || cam == null) return;

        playerCameras.put(player.getUniqueId(), cam);
        if (!playerLocationsCache.containsKey(player.getUniqueId())) {
            playerLocationsCache.put(player.getUniqueId(), new BasicLocation(player.getLocation()));
        }
        if (!playerGamemodesCache.containsKey(player.getUniqueId())) {
            playerGamemodesCache.put(player.getUniqueId(), player.getGameMode());
        }
    }

    public void removePlayer(Player player) {
        playerCameras.remove(player.getUniqueId());
        BasicLocation loc = playerLocationsCache.remove(player.getUniqueId());
        if (loc != null) {
            player.teleport(loc.getLocation());
        }
        GameMode gm = playerGamemodesCache.remove(player.getUniqueId());
        if (gm != null) {
            player.setGameMode(gm);
        }
    }

    public void addCam(CamInfo cam) {
        if (!cams.contains(cam)) {
            cams.add(cam);
            this.plugin.getDataFile().saveCam(cam);
        }
    }

    public void removeCam(CamInfo cam) {
        if (cams.contains(cam)) {
            cams.remove(cam);
            this.plugin.getDataFile().removeCam(cam);
        }
    }

    public boolean isPlayerInCam(Player player) {
        return playerCameras.containsKey(player.getUniqueId());
    }

}
