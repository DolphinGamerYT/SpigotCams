package com.dolphln.spigotcams.models;

import java.util.UUID;

public class CamInfo {

    private final UUID uuid;
    private final String name;
    private final BasicLocation location;
    private final CamType camType;

    public CamInfo(final UUID uuid, final String name, final BasicLocation location, final CamType camType) {
        this.uuid = uuid;
        this.name = name;
        this.location = location;
        this.camType = camType;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public BasicLocation getLocation() {
        return this.location;
    }

    public CamType getCamType() {
        return this.camType;
    }

}
