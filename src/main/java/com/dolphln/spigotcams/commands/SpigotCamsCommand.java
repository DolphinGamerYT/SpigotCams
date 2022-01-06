package com.dolphln.spigotcams.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.dolphln.spigotcams.SpigotCams;

@CommandAlias("spigotcams")
@CommandPermission("spigotcams.command")
public class SpigotCamsCommand extends BaseCommand {

    private final SpigotCams plugin;

    public SpigotCamsCommand(SpigotCams plugin) {
        this.plugin = plugin;
    }



}
