package org.tarook.wallclimbing;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


@SuppressWarnings("unused") // Methods are used by WallClimbSystems
public class WallClimbComponent implements Component<EntityStore> {

    private boolean isWallClimbing;
    private boolean staminaDepleted;
    private float staminaDrainRateMultiplier = 1.0f;
    private float climbSpeedMultiplier = 1.0f;
    private Vector3d inputDirection = new Vector3d(0,0,0);

    @Nullable
    private Ref<EntityStore> entity;

    public static ComponentType<EntityStore, WallClimbComponent> getComponentType() {
        return WallClimbPlugin.get().getWallClimbComponentType();
    }

    public WallClimbComponent() {
    }

    public WallClimbComponent(@Nonnull WallClimbComponent other) {
        isWallClimbing = other.isWallClimbing;
        staminaDepleted = other.staminaDepleted;
        staminaDrainRateMultiplier = other.staminaDrainRateMultiplier;
        climbSpeedMultiplier = other.climbSpeedMultiplier;
        inputDirection = new Vector3d(other.inputDirection);
        entity = other.entity;
    }

    public boolean isWallClimbing() {
        return this.isWallClimbing;
    }

    public void setWallClimbing(boolean wallClimbing) {
        this.isWallClimbing = wallClimbing;
    }

    public boolean isStaminaDepleted() {
        return this.staminaDepleted;
    }

    public void setStaminaDepleted(boolean staminaDepleted) {
        this.staminaDepleted = staminaDepleted;
    }

    public float getStaminaDrainRateMultiplier() {
        return this.staminaDrainRateMultiplier;
    }

    public void setStaminaDrainRateMultiplier(float staminaDrainRateMultiplier) {
        this.staminaDrainRateMultiplier = staminaDrainRateMultiplier;
    }

    public float getClimbSpeedMultiplier() {
        return climbSpeedMultiplier;
    }

    public void setClimbSpeedMultiplier(float climbSpeedMultiplier) {
        this.climbSpeedMultiplier = climbSpeedMultiplier;
    }

    public void resetStaminaDepletion() {
        staminaDepleted = false;
    }

    public void setInputDirection(Vector3d direction) {
        inputDirection = direction;
        inputDirection.normalize();
    }

    public void setInputDirection(Position position) {
        inputDirection = new Vector3d(position.x, position.y, position.z);
        inputDirection.normalize();
    }

    public Vector3d getInputDirection() {
        return this.inputDirection;
    }

    @Nonnull
    @Override
    public Component<EntityStore> clone() {
        return new WallClimbComponent(this);
    }

    public void addedToStore(Ref<EntityStore> ref)
    {
        entity = ref;
    }

    public void removedFromStore()
    {
        entity.getStore().assertThread();
        entity = null;
    }

    @Nullable
    public Ref<EntityStore> getReference() {
        return entity != null && entity.isValid() ? entity : null;
    }
}
