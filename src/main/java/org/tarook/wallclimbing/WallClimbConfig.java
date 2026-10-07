package org.tarook.wallclimbing;

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
            .append(new KeyedCodec<String[]>("BlacklistedBlocks", Codec.STRING_ARRAY),
                    (config, value) -> config.blacklistedBlocks = new HashSet<>(Arrays.asList(value)),
                    (config) -> config.blacklistedBlocks.toArray(new String[0])).add()
            .build();

    private float baseStaminaDrainRate = 10.0f;
    private float baseMinimumStaminaToClimb = 1.0f;
    private float baseClimbSpeed = 5.0f;
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

    public void addBlacklistedBlock(String blockName) {
        blacklistedBlocks.add(blockName);
    }

    public void removeBlacklistedBlock(String blockName) {
        blacklistedBlocks.remove(blockName);
    }
}
