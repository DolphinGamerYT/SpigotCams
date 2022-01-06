package com.dolphln.spigotcams.core;

import com.dolphln.spigotcams.SpigotCams;
import com.dolphln.spigotcams.models.BasicLocation;
import com.dolphln.spigotcams.models.CamInfo;
import org.bukkit.Bukkit;
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

        /*this.teleportTaskId = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            this.playerCameras.forEach((uuid, cam) -> {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player == null) return;

                player.teleport(cam.getLocation().getLocation());
            });
        }, 0L, 1L).getTaskId();*/

        this.loadCams();
    }

    private void loadCams() {
        cams.clear();
        cams.addAll(this.plugin.getDataFile().getAllCams());
    }

    public boolean addPlayer(Player player, CamInfo cam) {
        if (player == null || cam == null) return false;

        playerCameras.put(player.getUniqueId(), cam);
        if (!playerLocationsCache.containsKey(player.getUniqueId())) {
            playerLocationsCache.put(player.getUniqueId(), new BasicLocation(player.getLocation()));
        }
        if (!playerGamemodesCache.containsKey(player.getUniqueId())) {
            playerGamemodesCache.put(player.getUniqueId(), player.getGameMode());
        }
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(cam.getLocation().getLocation());
        return true;
    }

    public boolean removePlayer(Player player) {
        CamInfo cam = playerCameras.remove(player.getUniqueId());
        if (cam == null) return false;
        BasicLocation loc = playerLocationsCache.remove(player.getUniqueId());
        if (loc != null) {
            player.teleport(loc.getLocation());
        }
        GameMode gm = playerGamemodesCache.remove(player.getUniqueId());
        if (gm != null) {
            player.setGameMode(gm);
        }
        return true;
    }

    public void addCam(CamInfo cam) {
        if (cams.stream().noneMatch(c -> c.getUuid().equals(cam.getUuid()))) {
            cams.add(cam);
            this.plugin.getDataFile().saveCam(cam);
        }
    }

    public void removeCam(CamInfo removedCam) {
        if (cams.contains(removedCam)) {
            cams.remove(removedCam);
            this.plugin.getDataFile().removeCam(removedCam);
            this.playerCameras.forEach((uuid, cam) -> {
                if (!cam.getUuid().equals(removedCam.getUuid())) return;
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    removePlayer(player);
                }
            });
        }
    }

    public boolean isPlayerInCam(Player player) {
        return playerCameras.containsKey(player.getUniqueId());
    }

    public CamInfo getPlayerCam(Player player) {
        return playerCameras.get(player.getUniqueId());
    }

    public CamInfo getCam(UUID uuid) {
        return cams.stream().filter(c -> c.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public List<CamInfo> getCams() {
        return new ArrayList<>(cams);
    }

}
