package com.dolphln.spigotcams;

import co.aikar.commands.PaperCommandManager;
import com.dolphln.spigotcams.commands.SpigotCamsCommand;
import com.dolphln.spigotcams.core.CameraManager;
import com.dolphln.spigotcams.files.DataFile;
import com.dolphln.spigotcams.listener.CamListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;

public final class SpigotCams extends JavaPlugin {

    private static SpigotCams instance;

    private DataFile dataFile;

    private CameraManager cameraManager;

    @Override
    public void onEnable() {
        instance = this;

        this.dataFile = new DataFile(this);

        this.cameraManager = new CameraManager(this);

        this.registerCommands();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        this.dataFile.save();
    }

    private void registerCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);

        commandManager.getCommandCompletions().registerAsyncCompletion("cameras", (
                        context -> this.cameraManager.getCams().stream()
                        .map(cam -> cam.getUuid().toString())
                        .collect(Collectors.toList()))
        );

        commandManager.registerCommand(new SpigotCamsCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new CamListener(this), this);
    }

    public static SpigotCams getInstance() {
        return instance;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }
}
