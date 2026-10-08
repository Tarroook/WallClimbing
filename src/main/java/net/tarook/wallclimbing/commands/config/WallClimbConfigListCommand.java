package net.tarook.wallclimbing.commands.config;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.util.Config;
import net.tarook.wallclimbing.WallClimbConfig;

import javax.annotation.Nonnull;

public class WallClimbConfigListCommand extends CommandBase {

    private final Config<WallClimbConfig> config;

    public WallClimbConfigListCommand(Config<WallClimbConfig> config) {
        super("list", "List all configuration values");
        this.config = config;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        WallClimbConfig cfg = config.get();

        Message message = Message.raw("=== WallClimb Config ===")
                .insert("\n")
                .insert("BaseStaminaDrainRate: " + cfg.getBaseStaminaDrainRate())
                .insert("\n")
                .insert("BaseMinimumStaminaToClimb: " + cfg.getBaseMinimumStaminaToClimb())
                .insert("\n")
                .insert("BaseClimbSpeed: " + cfg.getBaseClimbSpeed())
                .insert("\n")
                .insert("blacklistedBlocks: " + cfg.getBlacklistedBlocks());

        context.sendMessage(message);
    }
}
