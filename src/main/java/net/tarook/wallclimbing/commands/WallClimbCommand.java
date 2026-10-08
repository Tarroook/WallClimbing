package net.tarook.wallclimbing.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.util.Config;
import net.tarook.wallclimbing.WallClimbConfig;
import net.tarook.wallclimbing.commands.config.WallClimbConfigCommand;

public class WallClimbCommand extends AbstractCommandCollection {

    public WallClimbCommand(Config<WallClimbConfig> config) {
        super("wallclimb", "Wall climbing plugin commands");

        // Add "config" subcommand
        this.addSubCommand(new WallClimbConfigCommand(config));
    }
}