package com.dolphln.spigotcams.models;

import org.bukkit.entity.EntityType;

public enum CamType {

    NORMAL(null),
    CREEPER(EntityType.CREEPER),
    ENDERMAN(EntityType.ENDERMAN),
    SPIDER(EntityType.SPIDER),;

    private EntityType entityType;

    CamType(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

}
