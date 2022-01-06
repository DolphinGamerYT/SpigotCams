package com.dolphln.spigotcams;

import com.dolphln.spigotcams.core.CameraManager;
import com.dolphln.spigotcams.files.DataFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotCams extends JavaPlugin {


    private DataFile dataFile;

    private CameraManager cameraManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.dataFile = new DataFile(this);

        this.cameraManager = new CameraManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }
}
