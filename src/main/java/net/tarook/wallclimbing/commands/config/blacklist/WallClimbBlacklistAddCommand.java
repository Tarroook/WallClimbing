package net.tarook.wallclimbing.commands.config.blacklist;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import net.tarook.wallclimbing.WallClimbConfig;

public class WallClimbBlacklistAddCommand extends CommandBase {

    private final Config<WallClimbConfig> config;

    private final RequiredArg<String> blockArg = this.withRequiredArg("blockID", "Block ID, case sensitive.", ArgTypes.STRING);


    public WallClimbBlacklistAddCommand(Config<WallClimbConfig> config) {
        super("add", "Add a block to the wall climbing blacklist");
        this.config = config;
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        String blockID = blockArg.get(commandContext);
        WallClimbConfig cfg = config.get();

        if (cfg.getBlacklistedBlocks().contains(blockID)) {
            commandContext.sendMessage(Message.raw("Block " + blockID + " is already blacklisted."));
            return;
        }

        cfg.getBlacklistedBlocks().add(blockID);
        config.save();
        commandContext.sendMessage(Message.raw("Added block " + blockID + " to the wall climbing blacklist."));
    }
}
