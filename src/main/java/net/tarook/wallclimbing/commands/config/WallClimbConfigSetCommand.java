package net.tarook.wallclimbing.commands.config;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.util.Config;
import net.tarook.wallclimbing.WallClimbConfig;

import javax.annotation.Nonnull;

public class WallClimbConfigSetCommand extends AbstractCommandCollection {

    private final Config<WallClimbConfig> config;

    public WallClimbConfigSetCommand(Config<WallClimbConfig> config) {
        super("set", "Set a configuration value");
        this.config = config;

        addSubCommand(new BaseStaminaDrainRateCommand());
        addSubCommand(new BaseMinimumStaminaToClimbCommand());
        addSubCommand(new BaseClimbSpeedCommand());
        addSubCommand(new BaseClimbStillSpeedMultiplierCommand());
        addSubCommand(new MinVerticalSpeedToClimbCommand());
        addSubCommand(new SlowDownThresholdPercentageCommand());
        addSubCommand(new MinClimbSpeedCommand());
        addSubCommand(new MinDrainRateCommand());
        addSubCommand(new ClimbDetachLookThresholdCommand());
        addSubCommand(new ClimbStillThresholdCommand());
    }

    // region Subcommands for each configuration value
    // region General

    public class BaseStaminaDrainRateCommand extends CommandBase {
        private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

        public BaseStaminaDrainRateCommand() {
            super("BaseStaminaDrainRate", "Set the base stamina drain rate");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            float value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setBaseStaminaDrainRate(value);
            config.save();
            context.sendMessage(Message.raw("Set BaseStaminaDrainRate to " + value));
        }
    }

    public class BaseMinimumStaminaToClimbCommand extends CommandBase {
        private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

        public BaseMinimumStaminaToClimbCommand() {
            super("baseminimumstaminatoclimb", "Set the base minimum stamina to climb");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            float value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setBaseMinimumStaminaToClimb(value);
            config.save();
            context.sendMessage(Message.raw("Set BaseMinimumStaminaToClimb to " + value));
        }
    }

    public class BaseClimbSpeedCommand extends CommandBase {
        private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

        public BaseClimbSpeedCommand() {
            super("BaseClimbSpeed", "Set the base climb speed");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            float value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setBaseClimbSpeed(value);
            config.save();
            context.sendMessage(Message.raw("Set BaseClimbSpeed to " + value));
        }
    }

    public class BaseClimbStillSpeedMultiplierCommand extends CommandBase {
        private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

        public BaseClimbStillSpeedMultiplierCommand() {
            super("BaseClimbStillSpeedMultiplier", "Set the base climb still speed multiplier");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            float value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setBaseClimbStillSpeedMultiplier(value);
            config.save();
            context.sendMessage(Message.raw("Set BaseClimbStillSpeedMultiplier to " + value));
        }
    }

    public class MinVerticalSpeedToClimbCommand extends CommandBase {
        private final RequiredArg<Double> valueArg = this.withRequiredArg("value", "New value", ArgTypes.DOUBLE);

        public MinVerticalSpeedToClimbCommand() {
            super("MinVerticalSpeedToClimb", "Set the minimum vertical speed to climb");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            double value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setMinVerticalSpeedToClimb(value);
            config.save();
            context.sendMessage(Message.raw("Set MinVerticalSpeedToClimb to " + value));
        }
    }

    // endregion

    // region Struggle climbing

    public class SlowDownThresholdPercentageCommand extends CommandBase {
        private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

        public SlowDownThresholdPercentageCommand() {
            super("SlowdownThresholdPercentage", "Set the slow down threshold percentage");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            float value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setSlowDownThresholdPercentage(value);
            config.save();
            context.sendMessage(Message.raw("Set SlowDownThresholdPercentage to " + value));
        }
    }

    public class MinClimbSpeedCommand extends CommandBase {
        private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

        public MinClimbSpeedCommand() {
            super("MinClimbSpeed", "Set the minimum climb speed");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            float value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setMinClimbSpeed(value);
            config.save();
            context.sendMessage(Message.raw("Set MinClimbSpeed to " + value));
        }
    }

    public class MinDrainRateCommand extends CommandBase {
        private final RequiredArg<Float> valueArg = this.withRequiredArg("value", "New value", ArgTypes.FLOAT);

        public MinDrainRateCommand() {
            super("MinDrainRate", "Set the minimum drain rate");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            float value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setMinDrainRate(value);
            config.save();
            context.sendMessage(Message.raw("Set MinDrainRate to " + value));
        }
    }

    // endregion

    // region Mouse look thresholds

    public class ClimbDetachLookThresholdCommand extends CommandBase {
        private final RequiredArg<Double> valueArg = this.withRequiredArg("value", "New value", ArgTypes.DOUBLE);

        public ClimbDetachLookThresholdCommand() {
            super("ClimbDetachLookThreshold", "Set the climb detach look threshold");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            double value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setClimbDetachLookThreshold(value);
            config.save();
            context.sendMessage(Message.raw("Set ClimbDetachLookThreshold to " + value));
        }
    }

    public class ClimbStillThresholdCommand extends CommandBase {
        private final RequiredArg<Double> valueArg = this.withRequiredArg("value", "New value", ArgTypes.DOUBLE);

        public ClimbStillThresholdCommand() {
            super("ClimbStillThreshold", "Set the climb still threshold");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            double value = valueArg.get(context);
            WallClimbConfig cfg = config.get();
            cfg.setClimbStillThreshold(value);
            config.save();
            context.sendMessage(Message.raw("Set ClimbStillThreshold to " + value));
        }
    }

    // endregion

    // endregion
}
