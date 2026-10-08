package net.tarook.wallclimbing.commands.config.blacklist;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.util.Config;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import net.tarook.wallclimbing.WallClimbConfig;

public class WallClimbBlacklistRemoveCommand extends CommandBase {
    private final Config<WallClimbConfig> config;

    private final RequiredArg<String> blockArg = this.withRequiredArg("blockID", "Block ID, case sensitive.", ArgTypes.STRING);


    public WallClimbBlacklistRemoveCommand(Config<WallClimbConfig> config) {
        super("remove", "Remove a block from the wall climbing blacklist");
        this.config = config;
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext commandContext) {
        String blockID = blockArg.get(commandContext);
        WallClimbConfig cfg = config.get();

        if (cfg.getBlacklistedBlocks().contains(blockID)) {
            cfg.getBlacklistedBlocks().remove(blockID);
            config.save();
            commandContext.sendMessage(Message.raw("Removed block " + blockID + " from the wall climbing blacklist."));
        } else {
            commandContext.sendMessage(Message.raw("Block " + blockID + " is not in the blacklist."));
        }
    }
}
