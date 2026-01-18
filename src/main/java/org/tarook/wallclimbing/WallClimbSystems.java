package org.tarook.wallclimbing;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.protocol.ChangeVelocityType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesSystems;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketWatcher;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Systems for handling wall climbing mechanics.
 */
public class WallClimbSystems {

    public WallClimbSystems() {
    }

    /**
     * System that ensures players have the WallClimbComponent attached.
     */
    public static class EnsureWallClimbComponentSystem extends HolderSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType;

        public EnsureWallClimbComponentSystem(
                @Nonnull ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType) {
            this.wallClimbComponentType = wallClimbComponentType;
        }

        /*
        @Override
        public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            WallClimbComponent wallClimbComponent = store.ensureAndGetComponent(ref, this.wallClimbComponentType);
            wallClimbComponent.addedToStore(ref);
            PacketAdapters.registerInbound(wallClimbComponent);
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        }

         */

        @Override
        public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
            WallClimbComponent wallClimbComponent = store.ensureAndGetComponent(wallClimbComponentType)
            wallClimbComponent.addedToStore(holder);
            PacketAdapters.registerInbound(wallClimbComponent);
        }

        @Override
        public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store) {

        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType());
        }


    }

    /**
     * System that handles the wall climbing logic each tick.
     * Checks if the player can climb and applies climbing movement and stamina drain.
     */
    public static class WallClimbTickSystem extends EntityTickingSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType;

        public WallClimbTickSystem(
                @Nonnull ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType) {
            this.wallClimbComponentType = wallClimbComponentType;
        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(
                    Player.getComponentType(),
                    wallClimbComponentType,
                    MovementStatesComponent.getComponentType(),
                    EntityStatMap.getComponentType(),
                    HeadRotation.getComponentType(),
                    TransformComponent.getComponentType(),
                    Velocity.getComponentType(),
                    BoundingBox.getComponentType()
            );
        }

        @Nonnull
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(
                    new SystemDependency<>(Order.BEFORE, MovementStatesSystems.TickingSystem.class),
                    new SystemDependency<>(Order.BEFORE, EntityStatsModule.PlayerRegenerateStatsSystem.class)
            );
        }

        @Override
        public void tick(
                float dt,
                int index,
                @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
                @Nonnull Store<EntityStore> store,
                @Nonnull CommandBuffer<EntityStore> commandBuffer) {

            WallClimbComponent wallClimbComponent = archetypeChunk.getComponent(index, this.wallClimbComponentType);
            MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
            EntityStatMap entityStatMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            HeadRotation headRotation = archetypeChunk.getComponent(index, HeadRotation.getComponentType());
            Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
            PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());

            MovementStates movementStates = movementStatesComponent.getMovementStates();

            // Reset stamina depletion when player touches ground
            if (movementStates.onGround) {
                wallClimbComponent.resetStaminaDepletion();
            }

            // Check current stamina
            EntityStatValue staminaStat = entityStatMap.get(DefaultEntityStatTypes.getStamina());
            float currentStamina = staminaStat != null ? staminaStat.get() : 0.0f;

            boolean canClimb = canPlayerClimb(wallClimbComponent, movementStates, currentStamina, playerRef, store);

            if (canClimb && !wallClimbComponent.isWallClimbing()) {
                startClimbing(wallClimbComponent, movementStates);
            }
            else if (!canClimb && wallClimbComponent.isWallClimbing()) {
                stopClimbing(wallClimbComponent, movementStates);
            }
            else if (wallClimbComponent.isWallClimbing())
            {
                climb(dt, wallClimbComponent, movementStates, velocityComponent, currentStamina, entityStatMap, headRotation);
            }
        }

        private static void startClimbing(WallClimbComponent wallClimbComponent, MovementStates movementStates) {
            //WallClimbPlugin.getHytaleLogger().atInfo().log("Start Climbing Wall");
            wallClimbComponent.setWallClimbing(true);
            movementStates.climbing = true;
            movementStates.falling = false;
            movementStates.jumping = false;
        }

        private static void stopClimbing(WallClimbComponent wallClimbComponent, MovementStates movementStates) {
            //WallClimbPlugin.getHytaleLogger().atInfo().log("Stop Climbing Wall");
            wallClimbComponent.setWallClimbing(false);
            movementStates.climbing = false;
        }

        private static void climb(float dt, WallClimbComponent wallClimbComponent, MovementStates movementStates, Velocity velocityComponent, float currentStamina, EntityStatMap entityStatMap, HeadRotation headRotation) {
            //WallClimbPlugin.getHytaleLogger().atInfo().log("Is Climbing Wall");

            Vector3d force = headRotation.getDirection().normalize().scale(wallClimbComponent.getClimbSpeed());
            velocityComponent.addInstruction(force, null, ChangeVelocityType.Set);

            float staminaToDrain = wallClimbComponent.getStaminaDrainRate() * dt;
            float newStamina = currentStamina - staminaToDrain;

            if (newStamina <= 0.0f) {
                newStamina = 0.0f;
                wallClimbComponent.setStaminaDepleted(true);
                stopClimbing(wallClimbComponent, movementStates);
            }

            entityStatMap.setStatValue(DefaultEntityStatTypes.getStamina(), newStamina);
        }

        private boolean canPlayerClimb(
                @Nonnull WallClimbComponent wallClimbComponent,
                @Nonnull MovementStates movementStates,
                float currentStamina,
                @Nonnull PlayerRef playerRef,
                @Nonnull Store<EntityStore> store) {

            if (movementStates.onGround) {
                //WallClimbPlugin.getHytaleLogger().atInfo().log("Cannot climb while on ground");
                return false;
            }

            if (movementStates.mantling) {
                //WallClimbPlugin.getHytaleLogger().atInfo().log("Cannot climb while mantling");
                return false;
            }

            // Cannot climb if stamina is depleted until touching ground
            if (wallClimbComponent.isStaminaDepleted()) {
                //WallClimbPlugin.getHytaleLogger().atInfo().log("Stamina depleted");
                return false;
            }

            // Must have minimum stamina to start/continue climbing
            if (currentStamina < wallClimbComponent.getMinimumStaminaToClimb()) {
                //WallClimbPlugin.getHytaleLogger().atInfo().log("Insufficient stamina: " + currentStamina);
                return false;
            }

            // Cannot climb while in fluid or swimming
            if (movementStates.inFluid || movementStates.swimming) {
                //WallClimbPlugin.getHytaleLogger().atInfo().log("Is in fluid or swimming");
                return false;
            }

            // Cannot climb while flying or gliding
            if (movementStates.flying || movementStates.gliding) {
                //WallClimbPlugin.getHytaleLogger().atInfo().log("Is flying or gliding");
                return false;
            }

            // Check if there's a solid block in front of the player
            return isFacingSolidBlock(store, playerRef);
        }

        private boolean isFacingSolidBlock(@Nonnull Store<EntityStore> store, @Nonnull PlayerRef playerRef) {

            World world = store.getExternalData().getWorld();
            double checkDistance = 0.5; // Distance in front of player to check

            Vector3i targetBlockPos = TargetUtil.getTargetBlock(playerRef.getReference(), checkDistance, playerRef.getReference().getStore());
            if(targetBlockPos != null)
            {
                BlockType blockType = world.getBlockType(targetBlockPos);
                if (blockType != null && isSolidClimbableBlock(blockType)) {
                    //WallClimbPlugin.getHytaleLogger().atInfo().log("Facing solid block: " + blockType.getId() +
                    //        " at (" + targetBlockPos.x + ", " + targetBlockPos.y + ", " + targetBlockPos.z + ")");
                    return true;
                }
            }

            //WallClimbPlugin.getHytaleLogger().atInfo().log("Not facing any solid climbable block");
            return false;
        }


        private boolean isSolidClimbableBlock(BlockType blockType) {
            return blockType.getMaterial() == BlockMaterial.Solid;
        }

        /**
         * Checks if the player is currently on a ladder.
         * This prevents interfering with normal ladder climbing.
         */
        private boolean isOnLadder(
                @Nonnull TransformComponent transformComponent,
                @Nonnull Store<EntityStore> store) {

            World world = store.getExternalData().getWorld();
            Vector3d position = transformComponent.getPosition();

            int blockX = (int) Math.floor(position.x);
            int blockY = (int) Math.floor(position.y);
            int blockZ = (int) Math.floor(position.z);

            BlockType blockType = world.getBlockType(blockX, blockY, blockZ);

            if (blockType == null) {
                return false;
            }

            // Check if this block type is a ladder or climbable by default
            // Typically ladders have the "climbable" tag or property
            String blockId = blockType.getId();
            return blockId != null && (blockId.contains("ladder") || blockId.contains("vine"));
        }
    }
}
