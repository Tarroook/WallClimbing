package org.tarook.wallclimbing.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.util.Config;
import org.tarook.wallclimbing.WallClimbConfig;

public class WallClimbCommand extends AbstractCommandCollection {

    public WallClimbCommand(Config<WallClimbConfig> config) {
        super("wallclimb", "Wall climbing plugin commands");

        // Add "config" subcommand
        this.addSubCommand(new WallClimbConfigCommand(config));
    }
}