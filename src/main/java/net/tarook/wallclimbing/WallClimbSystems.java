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
import org.joml.Vector3d;
import org.joml.Vector3i;
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
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.core.util.TargetUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Set;
import javax.annotation.Nonnull;

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
            Velocity velocityComponent = archetypeChunk.getComponent(index, Velocity.getComponentType());
            PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
//          PlayerInput playerInputComponent = archetypeChunk.getComponent(index, PlayerInput.getComponentType());
            MovementStates movementStates = movementStatesComponent.getMovementStates();

            World world = store.getExternalData().getWorld();
            world.execute(() ->{
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

                boolean canClimb = canPlayerClimb(wallClimbComponent, movementStates, currentStamina, playerRef, store);

                if (canClimb && !wallClimbComponent.isWallClimbing()) {
                    startClimbing(wallClimbComponent, movementStates, entityStatMap, staminaStatIndex);
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

        private static void startClimbing(WallClimbComponent wallClimbComponent, MovementStates movementStates, EntityStatMap statMap, int staminaStatIndex) {
            wallClimbComponent.setWallClimbing(true);
            movementStates.climbing = true;
            movementStates.falling = false;
            movementStates.jumping = false;
            statMap.setStatChangeSuppressed(staminaStatIndex, StatChangeDirection.RAISE, true);
        }

        private static void stopClimbing(WallClimbComponent wallClimbComponent, MovementStates movementStates, EntityStatMap statMap, int staminaStatIndex) {
            wallClimbComponent.setWallClimbing(false);
            movementStates.climbing = false;
            statMap.setStatChangeSuppressed(staminaStatIndex, StatChangeDirection.RAISE, false);
        }

        private void climb(float dt, WallClimbComponent wallClimbComponent, MovementStates movementStates, Velocity velocityComponent, EntityStatMap entityStatMap, HeadRotation headRotation, int staminaStatIndex) {
            float speed = config.get().getBaseClimbSpeed() * wallClimbComponent.getClimbSpeedMultiplier();
            Vector3d force = headRotation.getDirection().normalize().mul(speed);
            velocityComponent.addInstruction(force, null, ChangeVelocityType.Set);

            float staminaToDrain = config.get().getBaseStaminaDrainRate() * wallClimbComponent.getStaminaDrainRateMultiplier() * dt;
            entityStatMap.addStatValue(staminaStatIndex, -staminaToDrain);

            EntityStatValue staminaStat = entityStatMap.get(staminaStatIndex);
            float currentStamina = staminaStat != null ? staminaStat.get() : 0.0f;

            if (currentStamina <= 0.0f) {
                wallClimbComponent.setStaminaDepleted(true);
                stopClimbing(wallClimbComponent, movementStates, entityStatMap, staminaStatIndex);
            }
        }

        private boolean canPlayerClimb(@Nonnull WallClimbComponent wallClimbComponent, @Nonnull MovementStates movementStates, float currentStamina, @Nonnull PlayerRef playerRef, @Nonnull Store<EntityStore> store) {

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

            return isFacingClimbableBlock(store, playerRef);
        }

        private boolean isFacingClimbableBlock(@Nonnull Store<EntityStore> store, @Nonnull PlayerRef playerRef) {
            BlockType blockType = getFacingBlockType(store, playerRef);
            return blockType != null && isSolidClimbableBlock(blockType);
        }

        private BlockType getFacingBlockType(@Nonnull Store<EntityStore> store, @Nonnull PlayerRef playerRef) {
            World world = store.getExternalData().getWorld();
            double checkDistance = 0.5;

            Vector3i targetBlockPos = TargetUtil.getTargetBlock(playerRef.getReference(), checkDistance, playerRef.getReference().getStore());
            if (targetBlockPos == null) {
                return null;
            }

            return world.getBlockType(targetBlockPos);
        }


        private boolean isSolidClimbableBlock(@Nonnull BlockType blockType) {
            return blockType.getMaterial() == BlockMaterial.Solid && !isBlackListedBlock(blockType);
        }

        private boolean isBlackListedBlock(@Nonnull BlockType blockType) {
            String blockId = blockType.getId();
            return config.get().getBlacklistedBlocks().contains(blockId);
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
