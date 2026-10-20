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

public class WallClimbConfigGetCommand extends AbstractCommandCollection {

    private final Config<WallClimbConfig> config;


    public WallClimbConfigGetCommand(Config<WallClimbConfig> config) {
        super("get", "Get a configuration value");
        this.config = config;

        addSubCommand(new BaseStaminaDrainRateCommand());
        addSubCommand(new BaseMinimumStaminaToClimbCommand());
        addSubCommand(new BaseClimbSpeedCommand());
        addSubCommand(new BaseClimbStillSpeedMultiplierCommand());
        addSubCommand(new MinVerticalSpeedToClimbCommand());
        addSubCommand(new BlacklistedBlocksCommand());
        addSubCommand(new SlowDownThresholdPercentageCommand());
        addSubCommand(new MinClimbSpeedCommand());
        addSubCommand(new MinDrainRateCommand());
        addSubCommand(new ClimbDetachLookThresholdCommand());
        addSubCommand(new ClimbStillThresholdCommand());
    }


    // region Subcommands for each configuration value
    // region General

    public class BaseStaminaDrainRateCommand extends CommandBase {
        public BaseStaminaDrainRateCommand() {
            super("BaseStaminaDrainRate", "Get the base stamina drain rate");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("BaseStaminaDrainRate = " + cfg.getBaseStaminaDrainRate()));
        }
    }

    public class BaseMinimumStaminaToClimbCommand extends CommandBase {
        public BaseMinimumStaminaToClimbCommand() {
            super("BaseMinimumStaminaToClimb", "Get the base minimum stamina to climb");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("BaseMinimumStaminaToClimb = " + cfg.getBaseMinimumStaminaToClimb()));
        }
    }

    public class BaseClimbSpeedCommand extends CommandBase {
        public BaseClimbSpeedCommand() {
            super("BaseClimbSpeed", "Get the base climb speed");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("BaseClimbSpeed = " + cfg.getBaseClimbSpeed()));
        }
    }

    public class BaseClimbStillSpeedMultiplierCommand extends CommandBase {
        public BaseClimbStillSpeedMultiplierCommand() {
            super("BaseClimbStillSpeedMultiplier", "Get the base climb still speed multiplier");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("BaseClimbStillSpeedMultiplier = " + cfg.getBaseClimbStillSpeedMultiplier()));
        }
    }

    public class MinVerticalSpeedToClimbCommand extends CommandBase {
        public MinVerticalSpeedToClimbCommand() {
            super("MinVerticalSpeedToClimb", "Get the minimum vertical speed to climb");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("MinVerticalSpeedToClimb = " + cfg.getMinVerticalSpeedToClimb()));
        }
    }

    public class BlacklistedBlocksCommand extends CommandBase {
        public BlacklistedBlocksCommand() {
            super("BlacklistedBlocks", "Get the blacklisted blocks");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("BlacklistedBlocks = " + cfg.getBlacklistedBlocks()));
        }
    }

    // endregion

    // region Struggle climbing

    public class SlowDownThresholdPercentageCommand extends CommandBase {
        public SlowDownThresholdPercentageCommand() {
            super("SlowdownThresholdPercentage", "Get the slow down threshold percentage");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("SlowDownThresholdPercentage = " + cfg.getSlowDownThresholdPercentage()));
        }
    }

    public class MinClimbSpeedCommand extends CommandBase {
        public MinClimbSpeedCommand() {
            super("MinClimbSpeed", "Get the minimum climb speed");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("MinClimbSpeed = " + cfg.getMinClimbSpeed()));
        }
    }

    public class MinDrainRateCommand extends CommandBase {
        public MinDrainRateCommand() {
            super("MinDrainRate", "Get the minimum drain rate");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("MinDrainRate = " + cfg.getMinDrainRate()));
        }
    }

    // endregion

    // region Mouse look thresholds

    public class ClimbDetachLookThresholdCommand extends CommandBase {
        public ClimbDetachLookThresholdCommand() {
            super("ClimbDetachLookThreshold", "Get the climb detach look threshold");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("ClimbDetachLookThreshold = " + cfg.getClimbDetachLookThreshold()));
        }
    }

    public class ClimbStillThresholdCommand extends CommandBase {
        public ClimbStillThresholdCommand() {
            super("ClimbStillThreshold", "Get the climb still threshold");
        }

        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            WallClimbConfig cfg = config.get();
            context.sendMessage(Message.raw("ClimbStillThreshold = " + cfg.getClimbStillThreshold()));
        }
    }

    // endregion

    // endregion
}