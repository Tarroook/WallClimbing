package org.tarook.wallclimbing.commands.config;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.util.Config;
import org.tarook.wallclimbing.WallClimbConfig;
import org.tarook.wallclimbing.commands.config.blacklist.WallClimbBlacklistCommand;

public class WallClimbConfigCommand extends AbstractCommandCollection {

    public WallClimbConfigCommand(Config<WallClimbConfig> config) {
        super("config", "Wall climbing configuration commands");

        requirePermission("wallclimb.admin");

        addSubCommand(new WallClimbConfigListCommand(config));
        addSubCommand(new WallClimbConfigSetCommand(config));
        addSubCommand(new WallClimbConfigGetCommand(config));
        addSubCommand(new WallClimbBlacklistCommand(config));
    }
}