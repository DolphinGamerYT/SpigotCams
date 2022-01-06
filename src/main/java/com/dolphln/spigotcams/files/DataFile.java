package com.dolphln.spigotcams.files;

import com.dolphln.spigotcams.SpigotCams;
import com.dolphln.spigotcams.models.BasicLocation;
import com.dolphln.spigotcams.models.CamInfo;
import com.dolphln.spigotcams.models.CamType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DataFile {

    private final SpigotCams plugin;

    private YamlConfiguration data;
    private File dataFile;

    public DataFile(SpigotCams plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            try {
                plugin.saveResource("data.yml", true);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create data.yml file.");
            }
        }

        data = YamlConfiguration.loadConfiguration(dataFile);

        plugin.getLogger().log(Level.FINE, "File data.yml loaded correctly.");

    }

    public YamlConfiguration getData() {
        return data;
    }

    public File getFile() {
        return dataFile;
    }

    public void save() {
        try {
            this.data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveLocation(String path, BasicLocation location) {
        this.data.set(path + ".x", location.getX());
        this.data.set(path + ".y", location.getY());
        this.data.set(path + ".z", location.getZ());
        this.data.set(path + ".yaw", location.getYaw());
        this.data.set(path + ".pitch", location.getPitch());
        this.data.set(path + ".world", location.getWorldName());
    }

    public BasicLocation getLocation(String path) {
        return new BasicLocation(
                this.data.getDouble(path + ".x"),
                this.data.getDouble(path + ".y"),
                this.data.getDouble(path + ".z"),
                this.data.getInt(path + ".yaw"),
                this.data.getInt(path + ".pitch"),
                this.data.getString(path + ".world")
        );
    }

    // MADE FUNCTIONS FOR EASY TO ACCESS PLUGIN

    public void saveCam(CamInfo info) {
        String path = "cams." + info.getUuid().toString() + ".";
        this.data.set(path + "name", info.getName());
        this.data.set(path + "type", info.getCamType().toString());
        this.saveLocation(path + "loc", info.getLocation());
    }

    public void removeCam(CamInfo info) {
        this.data.set("cams." + info.getUuid().toString(), null);
    }

    public CamInfo getCam(UUID uuid) {
        String path = "cams." + uuid.toString() + ".";
        if (this.data.contains(path)) {
            return new CamInfo(
                    UUID.fromString(this.data.getString(path + "name")),
                    this.data.getString(path + "type"),
                    this.getLocation(path + "loc"),
                    CamType.valueOf(this.data.getString(path + "type"))
            );
        }
        return null;
    }

    public List<UUID> getAllUUIDs() {
        List<UUID> uuids = new ArrayList<>();
        for (String key : this.data.getConfigurationSection("cams").getKeys(false)) {
            uuids.add(UUID.fromString(key));
        }
        return uuids;
    }

    public List<CamInfo> getAllCams() {
        List<CamInfo> cams = new ArrayList<>();
        for (UUID uuid : this.getAllUUIDs()) {
            cams.add(this.getCam(uuid));
        }
        return cams;
    }

}
