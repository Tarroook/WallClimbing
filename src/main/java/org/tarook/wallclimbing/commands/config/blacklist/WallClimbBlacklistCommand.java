package org.tarook.wallclimbing.commands.config.blacklist;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.util.Config;
import org.tarook.wallclimbing.WallClimbConfig;

public class WallClimbBlacklistCommand extends AbstractCommandCollection{
    public WallClimbBlacklistCommand(Config<WallClimbConfig> config) {
        super("blacklist", "Blacklist commands for wall climbing plugin");

        requirePermission("wallclimb.admin");

        addSubCommand(new WallClimbBlacklistAddCommand(config));
        addSubCommand(new WallClimbBlacklistRemoveCommand(config));
    }
}
