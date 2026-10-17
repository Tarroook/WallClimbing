package net.tarook.wallclimbing;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.Arrays;
import java.util.HashSet;

public class WallClimbConfig {

    public static final BuilderCodec<WallClimbConfig> CODEC = BuilderCodec.builder(WallClimbConfig.class, WallClimbConfig::new)
            .append(new KeyedCodec<Float>("BaseStaminaDrainRate", Codec.FLOAT),
                    (config, value) -> config.baseStaminaDrainRate = value,
                    (config) -> config.baseStaminaDrainRate).add()
            .append(new KeyedCodec<Float>("BaseMinimumStaminaToClimb", Codec.FLOAT),
                    (config, value) -> config.baseMinimumStaminaToClimb = value,
                    (config) -> config.baseMinimumStaminaToClimb).add()
            .append(new KeyedCodec<Float>("BaseClimbSpeed", Codec.FLOAT),
                    (config, value) -> config.baseClimbSpeed = value,
                    (config) -> config.baseClimbSpeed).add()
            .append(new KeyedCodec<Float>("BaseClimbStillSpeedMultiplier", Codec.FLOAT),
                    (config, value) -> config.baseClimbStillSpeedMultiplier = value,
                    (config) -> config.baseClimbStillSpeedMultiplier).add()
            .append(new KeyedCodec<Double>("ClimbDetachLookThreshold", Codec.DOUBLE),
                    (config, value) -> config.climbDetachLookThreshold = value,
                    (config) -> config.climbDetachLookThreshold).add()
            .append(new KeyedCodec<Double>("ClimbStillThreshold", Codec.DOUBLE),
                    (config, value) -> config.climbStillThreshold = value,
                    (config) -> config.climbStillThreshold).add()
            .append(new KeyedCodec<Double>("MinVerticalSpeedToClimb", Codec.DOUBLE),
                    (config, value) -> config.minVerticalSpeedToClimb = value,
                    (config) -> config.minVerticalSpeedToClimb).add()
            .append(new KeyedCodec<Float>("SlowDownThresholdPercentage", Codec.FLOAT),
                    (config, value) -> config.slowDownThresholdPercentage = value,
                    (config) -> config.slowDownThresholdPercentage).add()
            .append(new KeyedCodec<Float>("MinClimbSpeed", Codec.FLOAT),
                    (config, value) -> config.minClimbSpeed = value,
                    (config) -> config.minClimbSpeed).add()
            .append(new KeyedCodec<Float>("MinDrainRate", Codec.FLOAT),
                    (config, value) -> config.minDrainRate = value,
                    (config) -> config.minDrainRate).add()
            .append(new KeyedCodec<String[]>("BlacklistedBlocks", Codec.STRING_ARRAY),
                    (config, value) -> config.blacklistedBlocks = new HashSet<>(Arrays.asList(value)),
                    (config) -> config.blacklistedBlocks.toArray(new String[0])).add()
            .build();

    private float baseStaminaDrainRate = 4.0f;
    private float baseMinimumStaminaToClimb = 1.0f;
    private float baseClimbSpeed = 2.0f;
    private float baseClimbStillSpeedMultiplier = .1f;
    private double climbDetachLookThreshold = 0.d;
    private double climbStillThreshold = 0.7d;
    private double minVerticalSpeedToClimb = -30.d;
    private float slowDownThresholdPercentage = 0.2f;
    private float minClimbSpeed = 0.5f;
    private float minDrainRate = 0.01f;
    private HashSet<String> blacklistedBlocks = new HashSet<>(Arrays.asList(
            "Furniture_Village_Ladder",
            "Furniture_Ancient_Ladder",
            "Furniture_Frozen_Ladder",
            "Furniture_Jungle_Ladder",
            "Furniture_Crude_Ladder",
            "Furniture_Feran_Ladder",
            "Furniture_Lumberjack_Ladder",
            "Furniture_Temple_Scarak_Ladder",
            "Furniture_Desert_Ladder",
            "Furniture_Human_Ruins_Ladder",
            "Furniture_Tavern_Ladder",
            "Furniture_Temple_Light_Ladder",
            "Furniture_Temple_Wind_Ladder",
            "Furniture_Temple_Dark_Ladder",
            "Furniture_Kweebec_Ladder",
            "Furniture_Temple_Emerald_Ladder"
    ));

    public WallClimbConfig() {
    }

    public float getBaseStaminaDrainRate() {
        return baseStaminaDrainRate;
    }

    public float getBaseMinimumStaminaToClimb() {
        return baseMinimumStaminaToClimb;
    }

    public float getBaseClimbSpeed() {
        return baseClimbSpeed;
    }

    public float getBaseClimbStillSpeedMultiplier() {
        return baseClimbStillSpeedMultiplier;
    }

    public double getClimbDetachLookThreshold() {
        return climbDetachLookThreshold;
    }

    public double getClimbStillThreshold() {
        return climbStillThreshold;
    }

    public HashSet<String> getBlacklistedBlocks() { return blacklistedBlocks; }

    public void setBaseStaminaDrainRate(float baseStaminaDrainRate) {
        this.baseStaminaDrainRate = baseStaminaDrainRate;
    }

    public void setBaseMinimumStaminaToClimb(float baseMinimumStaminaToClimb) {
        this.baseMinimumStaminaToClimb = baseMinimumStaminaToClimb;
    }

    public void setBaseClimbSpeed(float baseClimbSpeed) {
        this.baseClimbSpeed = baseClimbSpeed;
    }

    public void setBaseClimbStillSpeedMultiplier(float baseClimbStillSpeedMultiplier) {
        this.baseClimbStillSpeedMultiplier = baseClimbStillSpeedMultiplier;
    }

    public void setClimbDetachLookThreshold(double climbDetachLookThreshold) {
        this.climbDetachLookThreshold = climbDetachLookThreshold;
    }

    public double getMinVerticalSpeedToClimb() {
        return minVerticalSpeedToClimb;
    }

    public void setMinVerticalSpeedToClimb(double minVerticalSpeedToClimb) {
        this.minVerticalSpeedToClimb = minVerticalSpeedToClimb;
    }

    public float getSlowDownThresholdPercentage() {
        return slowDownThresholdPercentage;
    }

    public void setSlowDownThresholdPercentage(float slowDownThresholdPercentage) {
        this.slowDownThresholdPercentage = slowDownThresholdPercentage;
    }

    public float getMinClimbSpeed() {
        return minClimbSpeed;
    }

    public void setMinClimbSpeed(float minClimbSpeed) {
        this.minClimbSpeed = minClimbSpeed;
    }

    public float getMinDrainRate() {
        return minDrainRate;
    }

    public void setMinDrainRate(float minDrainRate) {
        this.minDrainRate = minDrainRate;
    }

    public void setClimbStillThreshold(double climbStillThreshold) {
        this.climbStillThreshold = climbStillThreshold;
    }

    public void addBlacklistedBlock(String blockName) {
        blacklistedBlocks.add(blockName);
    }

    public void removeBlacklistedBlock(String blockName) {
        blacklistedBlocks.remove(blockName);
    }
}
