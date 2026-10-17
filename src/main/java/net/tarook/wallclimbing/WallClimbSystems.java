package net.tarook.wallclimbing;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.modules.entitystats.StatChangeDirection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import net.tarook.utils.Easing;
import org.joml.Vector3d;
import com.hypixel.hytale.protocol.*;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesSystems;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PacketWatcher;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.hypixel.hytale.math.util.ChunkUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.joml.Vector3i;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WallClimbSystems {

    public WallClimbSystems() {
    }

    public static class WallClimbHolderSystem extends HolderSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType;

        public WallClimbHolderSystem(@Nonnull ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType) {
            this.wallClimbComponentType = wallClimbComponentType;
        }

        @Override
        public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
            holder.ensureComponent(wallClimbComponentType);
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


    public static class WallClimbRefSystem extends RefSystem<EntityStore> {
        @Nonnull
        private final ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType;

        public WallClimbRefSystem(
                @Nonnull ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType) {
            this.wallClimbComponentType = wallClimbComponentType;
        }

        @Override
        public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
            WallClimbComponent wallClimbComponent = store.getComponent(ref, wallClimbComponentType);
            wallClimbComponent.addedToStore(ref);
        }

        @Override
        public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        }

        @Nonnull
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(Player.getComponentType(), wallClimbComponentType);
        }
    }


    public static class WallClimbTickSystem extends EntityTickingSystem<EntityStore> implements PacketWatcher {

        @Nonnull
        private final ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType;

        @Nonnull
        private final Config<WallClimbConfig> config;

        public WallClimbTickSystem(@Nonnull ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType, Config<WallClimbConfig> config) {
            this.wallClimbComponentType = wallClimbComponentType;
            this.config = config;
            PacketAdapters.registerInbound(this);
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
                    PlayerInput.getComponentType()
            );
        }

        @Nonnull
        @Override
        public Set<Dependency<EntityStore>> getDependencies() {
            return Set.of(
                    new SystemDependency<>(Order.BEFORE, MovementStatesSystems.TickingSystem.class),
                    new SystemDependency<>(Order.BEFORE, EntityStatsModule.PlayerRegenerateStatsSystem.class),
                    new SystemDependency<>(Order.BEFORE, PlayerSystems.ProcessPlayerInput.class)
            );
        }

        @Override
        public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

            WallClimbComponent wallClimbComponent = archetypeChunk.getComponent(index, wallClimbComponentType);
            MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
            EntityStatMap entityStatMap = archetypeChunk.getComponent(index, EntityStatMap.getComponentType());
            HeadRotation headRotation = archetypeChunk.getComponent(index, HeadRotation.getComponentType());
            TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());
            Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
            PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
//          PlayerInput playerInputComponent = archetypeChunk.getComponent(index, PlayerInput.getComponentType());
            MovementStates movementStates = movementStatesComponent.getMovementStates();

            store.getExternalData().getWorld().execute(() ->{
                // Reset stamina depletion when player touches ground
                if (movementStates.onGround) {
                    wallClimbComponent.resetStaminaDepletion();
                }

//            List<PlayerInput.InputUpdate> queue = playerInputComponent.getMovementUpdateQueue();
//            for (PlayerInput.InputUpdate update : queue) {
//
//                WallClimbPlugin.getHytaleLogger().atInfo().log(update.toString());
//                if (update instanceof PlayerInput.WishMovement wish) {
//                    double inputX = wish.getX();  // Horizontal left/right
//                    double inputZ = wish.getZ();  // Horizontal forward/back
//                    double inputY = wish.getY();  // Vertical (jump/fly)
//
//                    WallClimbPlugin.getHytaleLogger().atInfo().log("Player Input - X: " + inputX + ", Y: " + inputY + ", Z: " + inputZ);
//                }
//            }

                int staminaStatIndex = DefaultEntityStatTypes.getStamina();
                EntityStatValue staminaStat = entityStatMap.get(staminaStatIndex);
                float currentStamina = staminaStat != null ? staminaStat.get() : 0.0f;

                boolean canClimb = canPlayerClimb(wallClimbComponent, movementStates, currentStamina, headRotation, transformComponent, playerRef, store, velocityComponent);

                if (canClimb && !wallClimbComponent.isWallClimbing()) {
                    startClimbing(wallClimbComponent, movementStates, entityStatMap, transformComponent, playerRef, store, staminaStatIndex);
                }
                else if (!canClimb && wallClimbComponent.isWallClimbing()) {
                    stopClimbing(wallClimbComponent, movementStates, entityStatMap, staminaStatIndex);
                }
                else if (wallClimbComponent.isWallClimbing())
                {
                    climb(dt, wallClimbComponent, movementStates, velocityComponent, entityStatMap, headRotation, staminaStatIndex);
                }
            });
        }

        private static void startClimbing(WallClimbComponent wallClimbComponent, MovementStates movementStates, EntityStatMap statMap, TransformComponent transformComponent, PlayerRef playerRef, Store<EntityStore> store, int staminaStatIndex) {
            Vector3i climbFaceDirection = getClimbFaceDirection(transformComponent, playerRef, store);
            wallClimbComponent.setClimbingFaceDirection(climbFaceDirection);
            wallClimbComponent.setWallClimbing(true);
            movementStates.climbing = true;
            movementStates.falling = false;
            movementStates.jumping = false;
            statMap.setStatChangeSuppressed(staminaStatIndex, StatChangeDirection.RAISE, true);
        }

        private static void stopClimbing(WallClimbComponent wallClimbComponent, MovementStates movementStates, EntityStatMap statMap, int staminaStatIndex) {
            wallClimbComponent.setWallClimbing(false);
            wallClimbComponent.clearClimbingFaceDirection();
            movementStates.climbing = false;
            statMap.setStatChangeSuppressed(staminaStatIndex, StatChangeDirection.RAISE, false);
        }

        private void climb(float dt, WallClimbComponent wallClimbComponent, MovementStates movementStates, Velocity velocityComponent, EntityStatMap entityStatMap, HeadRotation headRotation, int staminaStatIndex) {
            EntityStatValue staminaStat = entityStatMap.get(staminaStatIndex);
            float speed = config.get().getBaseClimbSpeed() * wallClimbComponent.getClimbSpeedMultiplier();
            speed = getValueBasedOnStamina(Easing.CUBIC_IN, 0.2f, speed, staminaStat.get(), staminaStat.getMax(), 0.5f);
            Vector3d climbDirection = getClimbDirection(wallClimbComponent, headRotation);
            boolean isStill = climbDirection.lengthSquared() == 0.0d;
            Vector3d force = isStill ? new Vector3d(0.0d, 0.0d, 0.0d) : climbDirection.mul(speed);
            velocityComponent.addInstruction(force, null, ChangeVelocityType.Set);

            float staminaMultiplier = isStill ? wallClimbComponent.getClimbStillSpeedModifier() * config.get().getBaseClimbStillSpeedMultiplier() : 1.0f;
            float staminaToDrain = config.get().getBaseStaminaDrainRate() * wallClimbComponent.getStaminaDrainRateMultiplier() * staminaMultiplier * dt;
            staminaToDrain = getValueBasedOnStamina(Easing.CUBIC_IN, 0.2f, staminaToDrain, staminaStat.get(), staminaStat.getMax(), 0.01f);
            entityStatMap.addStatValue(staminaStatIndex, -staminaToDrain);

            float currentStamina = staminaStat != null ? staminaStat.get() : 0.0f;

            if (currentStamina <= 0.0f) {
                wallClimbComponent.setStaminaDepleted(true);
                stopClimbing(wallClimbComponent, movementStates, entityStatMap, staminaStatIndex);
            }
        }

        @Nonnull
        private Vector3d getClimbDirection(@Nonnull WallClimbComponent wallClimbComponent, @Nonnull HeadRotation headRotation) {
            Vector3d lookDirection = new Vector3d(headRotation.getDirection());
            if (lookDirection.lengthSquared() == 0.0d) {
                return new Vector3d(0.0d, 1.0d, 0.0d);
            }

            Vector3d wallNormal = getClimbFaceNormal(wallClimbComponent);
            if (wallNormal == null || wallNormal.lengthSquared() == 0.0d) {
                return lookDirection.normalize();
            }

            wallNormal.normalize();

            Vector3d normalizedLook = lookDirection.normalize();
            if (normalizedLook.dot(wallNormal) >= config.get().getClimbStillThreshold()) {
                return new Vector3d(0.0d, 0.0d, 0.0d);
            }

            Vector3d projectedLook = projectOntoPlane(normalizedLook, wallNormal);
            if (projectedLook.lengthSquared() == 0.0d) {
                return new Vector3d(0.0d, 0.0d, 0.0d);
            }

            return projectedLook.normalize();
        }

        @Nullable
        private Vector3d getClimbFaceNormal(@Nonnull WallClimbComponent wallClimbComponent) {
            Vector3i climbFaceDirection = wallClimbComponent.getClimbingFaceDirection();
            if (climbFaceDirection.lengthSquared() == 0) {
                return null;
            }

            return new Vector3d(climbFaceDirection.x, climbFaceDirection.y, climbFaceDirection.z);
        }

        @Nonnull
        private static Vector3i getClimbFaceDirection(@Nonnull TransformComponent transformComponent, @Nonnull PlayerRef playerRef, @Nonnull Store<EntityStore> store) {
            Vector3d climbFaceNormal = getClimbFaceNormal(transformComponent, playerRef);
            if (climbFaceNormal == null || climbFaceNormal.lengthSquared() == 0.0d) {
                return new Vector3i(0, 0, 0);
            }

            return new Vector3i(
                    (int) -Math.signum(climbFaceNormal.x),
                    (int) -Math.signum(climbFaceNormal.y),
                    (int) -Math.signum(climbFaceNormal.z)
            );
        }

        @Nullable
        private static Vector3d getClimbFaceNormal(@Nonnull TransformComponent transformComponent, @Nonnull PlayerRef playerRef) {
            Vector3i targetBlockPos = getFacingBlockPosition(playerRef);
            if (targetBlockPos == null) {
                return null;
            }

            double targetDistance = 0.5d;
            Vector3d targetLocation = TargetUtil.getTargetLocation(playerRef.getReference(), targetDistance, playerRef.getReference().getStore());
            if (targetLocation != null) {
                Vector3d normalFromHit = getNormalFromTargetLocation(targetBlockPos, targetLocation);
                if (normalFromHit != null) {
                    return normalFromHit;
                }
            }

            return getNormalFromPlayerPosition(targetBlockPos, transformComponent.getPosition());
        }

        @Nonnull
        private Vector3d projectOntoPlane(@Nonnull Vector3d direction, @Nonnull Vector3d planeNormal) {
            Vector3d projected = new Vector3d(direction);
            double normalComponent = projected.dot(planeNormal);
            return projected.sub(new Vector3d(planeNormal).mul(normalComponent));
        }

        @Nullable
        private static Vector3d getNormalFromTargetLocation(@Nonnull Vector3i blockPos, @Nonnull Vector3d targetLocation) {
            double localX = targetLocation.x - blockPos.x;
            double localY = targetLocation.y - blockPos.y;
            double localZ = targetLocation.z - blockPos.z;

            double distanceToMinX = Math.abs(localX);
            double distanceToMaxX = Math.abs(1.0d - localX);
            double distanceToMinY = Math.abs(localY);
            double distanceToMaxY = Math.abs(1.0d - localY);
            double distanceToMinZ = Math.abs(localZ);
            double distanceToMaxZ = Math.abs(1.0d - localZ);

            double bestDistance = distanceToMinX;
            Vector3d normal = new Vector3d(localX <= 0.5d ? -1.0d : 1.0d, 0.0d, 0.0d);

            if (distanceToMaxX < bestDistance) {
                bestDistance = distanceToMaxX;
                normal.set(1.0d, 0.0d, 0.0d);
            }
            if (distanceToMinY < bestDistance) {
                bestDistance = distanceToMinY;
                normal.set(0.0d, -1.0d, 0.0d);
            }
            if (distanceToMaxY < bestDistance) {
                bestDistance = distanceToMaxY;
                normal.set(0.0d, 1.0d, 0.0d);
            }
            if (distanceToMinZ < bestDistance) {
                bestDistance = distanceToMinZ;
                normal.set(0.0d, 0.0d, -1.0d);
            }
            if (distanceToMaxZ < bestDistance) {
                normal.set(0.0d, 0.0d, 1.0d);
            }

            return normal;
        }

        @Nonnull
        private static Vector3d getNormalFromPlayerPosition(@Nonnull Vector3i blockPos, @Nonnull Vector3d playerPosition) {
            Vector3d blockCenter = new Vector3d(blockPos.x + 0.5d, blockPos.y + 0.5d, blockPos.z + 0.5d);
            Vector3d offset = new Vector3d(playerPosition).sub(blockCenter);

            double absX = Math.abs(offset.x);
            double absY = Math.abs(offset.y);
            double absZ = Math.abs(offset.z);
            if (absX >= absY && absX >= absZ) {
                return new Vector3d(Math.signum(offset.x) == 0.0d ? 1.0d : Math.signum(offset.x), 0.0d, 0.0d);
            }
            if (absY >= absX && absY >= absZ) {
                return new Vector3d(0.0d, Math.signum(offset.y) == 0.0d ? 1.0d : Math.signum(offset.y), 0.0d);
            }
            return new Vector3d(0.0d, 0.0d, Math.signum(offset.z) == 0.0d ? 1.0d : Math.signum(offset.z));
        }

        @Nullable
        private static Vector3i getFacingBlockPosition(@Nonnull PlayerRef playerRef) {
            double checkDistance = 0.5d;
            return TargetUtil.getTargetBlock(playerRef.getReference(), checkDistance, playerRef.getReference().getStore());
        }

        private boolean canPlayerClimb(@Nonnull WallClimbComponent wallClimbComponent, @Nonnull MovementStates movementStates, float currentStamina, @Nonnull HeadRotation headRotation, @Nonnull TransformComponent transformComponent, @Nonnull PlayerRef playerRef, @Nonnull Store<EntityStore> store, Velocity velocityComponent) {

            if (movementStates.onGround) {
                return false;
            }

            if (movementStates.mantling) {
                return false;
            }

            if(wallClimbComponent.isWallClimbing()){
                if(currentStamina <= 0.0f) {
                    return false;
                }
            }
            else{
                if (currentStamina < config.get().getBaseMinimumStaminaToClimb()) {
                    return false;
                }
            }

            if (wallClimbComponent.isStaminaDepleted()) {
                return false;
            }

            if (movementStates.inFluid || movementStates.swimming) {
                return false;
            }

            if (movementStates.flying || movementStates.gliding) {
                return false;
            }

            if (velocityComponent.getVelocity().y < -30.d) {
                return false;
            }

            if (wallClimbComponent.isWallClimbing()) {
                if (!isLookingWithinDetachThreshold(headRotation, wallClimbComponent)) {
                    return false;
                }

                return isFacingClimbableBlock(store, transformComponent, wallClimbComponent.getClimbingFaceDirection());
            }

            return isFacingClimbableBlock(store, playerRef);
        }

        private boolean isFacingClimbableBlock(@Nonnull Store<EntityStore> store, @Nonnull PlayerRef playerRef) {
            BlockType blockType = getFacingBlockType(store, playerRef);
            return blockType != null && isSolidClimbableBlock(blockType);
        }

        private boolean isFacingClimbableBlock(@Nonnull Store<EntityStore> store, @Nonnull TransformComponent transformComponent, @Nonnull Vector3i direction) {
            BlockType blockType = getFacingBlockType(store, transformComponent, direction);
            return blockType != null && isSolidClimbableBlock(blockType);
        }

        private BlockType getFacingBlockType(@Nonnull Store<EntityStore> store, @Nonnull PlayerRef playerRef) {
            Vector3i targetBlockPos = getFacingBlockPosition(playerRef);
            if (targetBlockPos == null) {
                return null;
            }

            return getBlockTypeAt(store, targetBlockPos);
        }

        @Nullable
        private BlockType getFacingBlockType(@Nonnull Store<EntityStore> store, @Nonnull TransformComponent transformComponent, @Nonnull Vector3i direction) {
            Vector3i targetBlockPos = getFacingBlockPosition(transformComponent, direction);
            if (targetBlockPos == null) {
                return null;
            }

            return getBlockTypeAt(store, targetBlockPos);
        }

        @Nullable
        private BlockType getBlockTypeAt(@Nonnull Store<EntityStore> store, @Nonnull Vector3i blockPos) {
            World world = store.getExternalData().getWorld();
            ChunkStore chunkStore = world.getChunkStore();
            Store<ChunkStore> chunkStoreAccessor = chunkStore.getStore();

            Ref<ChunkStore> chunkSectionRef = chunkStore.getChunkSectionReferenceAtBlock(blockPos.x, blockPos.y, blockPos.z);
            if (chunkSectionRef == null || !chunkSectionRef.isValid()) {
                return null;
            }

            BlockSection blockSection = chunkStoreAccessor.getComponent(chunkSectionRef, BlockSection.getComponentType());
            if (blockSection == null) {
                return null;
            }

            int localX = blockPos.x & ChunkUtil.SIZE_MASK;
            int localY = blockPos.y & ChunkUtil.SIZE_MASK;
            int localZ = blockPos.z & ChunkUtil.SIZE_MASK;
            int blockId = blockSection.get(localX, localY, localZ);
            return BlockType.getAssetMap().getAssetOrDefault(blockId, BlockType.UNKNOWN);
        }

        @Nullable
        private Vector3i getFacingBlockPosition(@Nonnull TransformComponent transformComponent, @Nonnull Vector3i direction) {
            if (direction.lengthSquared() == 0) {
                return null;
            }

            Vector3d position = transformComponent.getPosition();
            return new Vector3i(
                    (int) Math.floor(position.x + direction.x),
                    (int) Math.floor(position.y + direction.y),
                    (int) Math.floor(position.z + direction.z)
            );
        }

        private boolean isLookingWithinDetachThreshold(@Nonnull HeadRotation headRotation, @Nonnull WallClimbComponent wallClimbComponent) {
            Vector3d lookDirection = new Vector3d(headRotation.getDirection());
            if (lookDirection.lengthSquared() == 0.0d) {
                return true;
            }

            Vector3i climbFaceDirection = wallClimbComponent.getClimbingFaceDirection();
            if (climbFaceDirection.lengthSquared() == 0) {
                return true;
            }

            Vector3d climbFaceVector = new Vector3d(climbFaceDirection.x, climbFaceDirection.y, climbFaceDirection.z).normalize();
            double alignment = lookDirection.normalize().dot(climbFaceVector);
            return alignment >= config.get().getClimbDetachLookThreshold();
        }


        private boolean isSolidClimbableBlock(@Nonnull BlockType blockType) {
            return blockType.getMaterial() == BlockMaterial.Solid && !isBlackListedBlock(blockType);
        }

        private boolean isBlackListedBlock(@Nonnull BlockType blockType) {
            String blockId = blockType.getId();
            return config.get().getBlacklistedBlocks().contains(blockId);
        }

        public float getValueBasedOnStamina(Easing easingCurve, float thresholdPercentage, float value, float currentStamina, float maxStamina, float minValue) {
            float staminaFraction = Math.max(0.0f, Math.min(1.0f, currentStamina / maxStamina));

            if (staminaFraction > thresholdPercentage) {
                return value;
            }

            double remappedInput = staminaFraction / thresholdPercentage;

            // Evaluate your existing curve on the remapped input
            double speedMultiplier = easingCurve.apply(remappedInput);

            return Math.max(minValue, (float) (value * speedMultiplier));
        }

        @Override
        public void accept(PacketHandler packetHandler, Packet packet) {
            /*
            if (packetHandler instanceof GamePacketHandler gpHandler) {
                UUID worldUuid = gpHandler.getPlayerRef().getWorldUuid();
                if(worldUuid == null) {
                    return;
                }
                World world = Universe.get().getWorld(worldUuid);
                world.execute(() -> {
                    Store<EntityStore> store = gpHandler.getPlayerRef().getReference().getStore();
                    Ref<EntityStore> ref = gpHandler.getPlayerRef().getReference();
                    WallClimbComponent wallClimbComponent = store.getComponent(gpHandler.getPlayerRef().getReference(), this.wallClimbComponentType);
                    if(packet instanceof ClientMovement movementPacket){
                        HalfFloatPosition wishMovement = movementPacket.relativePosition;
                        if(wishMovement == null){
                            WallClimbPlugin.getHytaleLogger().atInfo().log("Player: " + gpHandler.getPlayerRef().getUsername() + " sent null wishMovement Packet");
                        }
                        else{
                            WallClimbPlugin.getHytaleLogger().atInfo().log("Player: " + gpHandler.getPlayerRef().getUsername() + " sent wishMovement Packet: (" + wishMovement.x + ", " + wishMovement.y + ", " + wishMovement.z + ")");
                        }
                        //wallClimbComponent.setInputDirection(movementPacket.wishMovement);
                        //WallClimbPlugin.getHytaleLogger().atInfo().log("Client wishMovement Packet: (" + wishMovement.x + ", " + wishMovement.y + ", " + wishMovement.z + ")");
                        //WallClimbPlugin.getHytaleLogger().atInfo().log("Updated input direction to: " + wallClimbComponent.getInputDirection());
                    }
                });
            }
            */
        }
    }
}
