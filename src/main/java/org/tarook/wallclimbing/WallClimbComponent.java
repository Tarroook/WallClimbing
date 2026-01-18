package org.tarook.wallclimbing;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Position;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Component that tracks the wall climbing state for an entity.
 * Attached to players to enable wall climbing functionality.
 */
@SuppressWarnings("unused") // Methods are used by WallClimbSystems
public class WallClimbComponent implements Component<EntityStore> {

    /**
     * Whether the entity is currently wall climbing.
     */
    private boolean isWallClimbing;
    private boolean staminaDepleted;
    private float staminaDrainRate = 10.0f;
    private float minimumStaminaToClimb = 1.0f;
    private float climbSpeed = 5.0f;
    private Vector3d inputDirection = new Vector3d(0,0,0);

    @Nullable
    private Ref<EntityStore> entity;

    public static ComponentType<EntityStore, WallClimbComponent> getComponentType() {
        return WallClimbPlugin.get().getWallClimbComponentType();
    }

    public WallClimbComponent() {
    }

    public WallClimbComponent(@Nonnull WallClimbComponent other) {
        this.isWallClimbing = other.isWallClimbing;
        this.staminaDepleted = other.staminaDepleted;
        this.staminaDrainRate = other.staminaDrainRate;
        this.minimumStaminaToClimb = other.minimumStaminaToClimb;
        this.climbSpeed = other.climbSpeed;
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

    public float getStaminaDrainRate() {
        return this.staminaDrainRate;
    }

    public void setStaminaDrainRate(float staminaDrainRate) {
        this.staminaDrainRate = staminaDrainRate;
    }

    public float getMinimumStaminaToClimb() {
        return this.minimumStaminaToClimb;
    }

    public void setMinimumStaminaToClimb(float minimumStaminaToClimb) {
        this.minimumStaminaToClimb = minimumStaminaToClimb;
    }

    public float getClimbSpeed() {
        return this.climbSpeed;
    }

    public void setClimbSpeed(float climbSpeed) {
        this.climbSpeed = climbSpeed;
    }

    public void resetStaminaDepletion() {
        this.staminaDepleted = false;
    }

    public void setInputDirection(Vector3d direction) {
        this.inputDirection = direction;
        this.inputDirection.normalize();
    }

    public void setInputDirection(Position position) {
        this.inputDirection = new Vector3d(position.x, position.y, position.z);
        this.inputDirection.normalize();
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
        this.entity = ref;
    }

    public void removedFromStore()
    {
        this.entity.getStore().assertThread();
        this.entity = null;
    }

    @Nullable
    public Ref<EntityStore> getReference() {
        return this.entity != null && this.entity.isValid() ? this.entity : null;
    }
}
