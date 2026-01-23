package org.tarook.wallclimbing;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

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
            .build();

    private float baseStaminaDrainRate = 10.0f;
    private float baseMinimumStaminaToClimb = 1.0f;
    private float baseClimbSpeed = 5.0f;

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

    public void setBaseStaminaDrainRate(float baseStaminaDrainRate) {
        this.baseStaminaDrainRate = baseStaminaDrainRate;
    }

    public void setBaseMinimumStaminaToClimb(float baseMinimumStaminaToClimb) {
        this.baseMinimumStaminaToClimb = baseMinimumStaminaToClimb;
    }

    public void setBaseClimbSpeed(float baseClimbSpeed) {
        this.baseClimbSpeed = baseClimbSpeed;
    }
}
