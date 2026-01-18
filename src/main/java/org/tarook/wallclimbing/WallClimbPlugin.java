package org.tarook.wallclimbing;

import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.example.plugin.ExampleCommand;

import javax.annotation.Nonnull;

/**
 * Plugin that allows players to climb any solid block when:
 * - They are colliding with the block
 * - They are facing the block
 * - They are holding the jump key (spacebar)
 *
 * Climbing drains stamina and requires stamina to initiate.
 * Once stamina runs out, climbing stops until the player touches ground.
 */
public class WallClimbPlugin extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(WallClimbPlugin.class)
            .depends(EntityModule.class)
            .depends(EntityStatsModule.class)
            .build();

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static WallClimbPlugin instance;
    private ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType;

    public WallClimbPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Initializing " + this.getName() + " version " + this.getManifest().getVersion().toString());
        instance = this;
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        this.wallClimbComponentType = this.getEntityStoreRegistry()
                .registerComponent(WallClimbComponent.class, WallClimbComponent::new);

        this.getCommandRegistry().registerCommand(new ExampleCommand(this.getName(), this.getManifest().getVersion().toString()));
        this.getCommandRegistry().registerCommand(new WallClimbTestCommand());
    }

    @Override
    protected void start() {
        this.getEntityStoreRegistry().registerSystem(
                new WallClimbSystems.WallClimbHolderSystem(this.wallClimbComponentType));
        this.getEntityStoreRegistry().registerSystem(
                new WallClimbSystems.WallClimbRefSystem(this.wallClimbComponentType));
        this.getEntityStoreRegistry().registerSystem(
                new WallClimbSystems.WallClimbTickSystem(this.wallClimbComponentType));
    }

    @Nonnull
    public ComponentType<EntityStore, WallClimbComponent> getWallClimbComponentType() {
        return this.wallClimbComponentType;
    }

    @Nonnull
    public static WallClimbPlugin get() {
        return instance;
    }

    public static HytaleLogger getHytaleLogger() {
        return LOGGER;
    }
}
