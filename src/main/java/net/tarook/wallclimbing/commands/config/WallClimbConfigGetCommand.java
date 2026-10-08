package net.tarook.wallclimbing.commands.config;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.util.Config;
import net.tarook.wallclimbing.WallClimbConfig;

import javax.annotation.Nonnull;

public class WallClimbConfigGetCommand extends CommandBase {

    private final Config<WallClimbConfig> config;

    private final RequiredArg<String> settingArg = this.withRequiredArg("setting", "Setting name", ArgTypes.STRING);

    public WallClimbConfigGetCommand(Config<WallClimbConfig> config) {
        super("get", "Get a configuration value");
        this.config = config;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        String setting = settingArg.get(context);
        WallClimbConfig cfg = config.get();

        String value = switch (setting.toLowerCase()) {
            case "basestaminadrainrate" -> String.valueOf(cfg.getBaseStaminaDrainRate());
            case "baseminimumstaminatoclimb" -> String.valueOf(cfg.getBaseMinimumStaminaToClimb());
            case "baseclimbspeed" -> String.valueOf(cfg.getBaseClimbSpeed());
            case "blacklistedblocks" -> String.valueOf(cfg.getBlacklistedBlocks());
            default -> null;
        };

        if (value != null) {
            context.sendMessage(Message.raw(setting + " = " + value));
        } else {
            context.sendMessage(Message.raw("Unknown setting: " + setting));
        }
    }
}