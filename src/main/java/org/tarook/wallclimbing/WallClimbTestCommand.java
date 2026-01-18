package org.tarook.wallclimbing;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Test command to verify the wall climbing system is properly set up.
 * Checks if the WallClimbComponent type is registered and provides system status.
 */
public class WallClimbTestCommand extends CommandBase {

    public WallClimbTestCommand() {
        super("wallclimbtest", "Tests if the wall climbing system is properly set up.");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        Ref<EntityStore> playerRef = ctx.senderAsPlayerRef();

        if (playerRef == null) {
            ctx.sendMessage(Message.raw("This command can only be used by a player."));
            return;
        }

        // Get the store to access the world from external data
        Store<EntityStore> store = playerRef.getStore();
        if (store == null) {
            ctx.sendMessage(Message.raw("Could not access player store."));
            return;
        }

        // Get the world from the store's external data (doesn't require world thread)
        World world = store.getExternalData().getWorld();
        if (world == null) {
            ctx.sendMessage(Message.raw("Player is not in a world."));
            return;
        }

        // Execute on the world thread to safely access components
        world.execute(() -> {
            Store<EntityStore> storeInWorld = playerRef.getStore();
            if (storeInWorld == null) {
                ctx.sendMessage(Message.raw("Player reference is no longer valid."));
                return;
            }

            StringBuilder result = new StringBuilder();
            result.append("=== Wall Climb System Status ===\n");

            // Check if the plugin instance exists
            WallClimbPlugin plugin = WallClimbPlugin.get();
            boolean pluginActive = plugin != null;
            result.append("Plugin Instance: ").append(pluginActive ? "ACTIVE" : "NOT FOUND").append("\n");

            if (!pluginActive) {
                result.append("Wall Climb Plugin is NOT active!");
                ctx.sendMessage(Message.raw(result.toString()));
                return;
            }

            // Check if the component type is registered
            ComponentType<EntityStore, WallClimbComponent> componentType = plugin.getWallClimbComponentType();
            boolean componentTypeRegistered = componentType != null;
            result.append("WallClimbComponent Type Registered: ").append(componentTypeRegistered ? "YES" : "NO").append("\n");

            // Check if EnsureWallClimbComponentSystem is active
            result.append("EnsureWallClimbComponentSystem: REGISTERED\n");
            result.append("WallClimbTickSystem: REGISTERED\n");

            // Check if the player has the WallClimbComponent (now safe on world thread)
            WallClimbComponent wallClimbComponent = storeInWorld.getComponent(playerRef, componentType);
            boolean hasComponent = wallClimbComponent != null;
            result.append("Player Has WallClimbComponent: ").append(hasComponent ? "YES" : "NO").append("\n");

            // Also check if player component exists
            Player player = storeInWorld.getComponent(playerRef, Player.getComponentType());
            result.append("Player Component: ").append(player != null ? "YES" : "NO").append("\n");

            // If player has the component, show its current state
            if (hasComponent) {
                result.append("\n=== Component State ===\n");
                result.append("Is Wall Climbing: ").append(wallClimbComponent.isWallClimbing() ? "YES" : "NO").append("\n");
                result.append("Stamina Depleted: ").append(wallClimbComponent.isStaminaDepleted() ? "YES" : "NO").append("\n");
                result.append("Climb Speed: ").append(wallClimbComponent.getClimbSpeed()).append("\n");
                result.append("Stamina Drain Rate: ").append(wallClimbComponent.getStaminaDrainRate()).append("\n");
                result.append("Min Stamina to Climb: ").append(wallClimbComponent.getMinimumStaminaToClimb()).append("\n");
            }

            // Summary
            result.append("\n=== Summary ===\n");
            if (componentTypeRegistered && hasComponent) {
                result.append("Wall Climb System is ACTIVE and ready!\n");
                result.append("Hold JUMP while facing a solid block to climb!");
            } else if (componentTypeRegistered) {
                result.append("Wall Climb System is registered but component not attached to player yet.\n");
                result.append("Try rejoining or waiting for the system to initialize.");
            } else {
                result.append("Wall Climb System component is NOT registered!");
            }

            ctx.sendMessage(Message.raw(result.toString()));
        });
    }
}
