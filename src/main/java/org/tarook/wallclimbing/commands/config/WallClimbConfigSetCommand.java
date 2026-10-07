package org.tarook.wallclimbing.commands.config;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.util.Config;
import org.tarook.wallclimbing.WallClimbConfig;

import javax.annotation.Nonnull;

public class WallClimbConfigSetCommand extends CommandBase {

    private final Config<WallClimbConfig> config;

    private final RequiredArg<String> settingArg = this.withRequiredArg("setting", "Setting name", ArgTypes.STRING);
    private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

    public WallClimbConfigSetCommand(Config<WallClimbConfig> config) {
        super("set", "Set a configuration value");
        this.config = config;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext context) {
        String setting = settingArg.get(context);
        float value = valueArg.get(context);

        WallClimbConfig cfg = config.get();

        boolean success = switch (setting.toLowerCase()) {
            case "basestaminadrainrate" -> { cfg.setBaseStaminaDrainRate(value); yield true; }
            case "baseminimumstaminatoclimb" -> { cfg.setBaseMinimumStaminaToClimb(value); yield true; }
            case "baseclimbspeed" -> { cfg.setBaseClimbSpeed(value); yield true; }
            default -> false;
        };

        if (success) {
            config.save();
            context.sendMessage(Message.raw("Set " + setting + " to " + value));
        } else {
            context.sendMessage(Message.raw("Unknown setting: " + setting));
        }
    }
}
