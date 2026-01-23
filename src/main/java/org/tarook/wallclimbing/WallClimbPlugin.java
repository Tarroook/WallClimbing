package org.tarook.wallclimbing;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import org.tarook.wallclimbing.commands.WallClimbCommand;

import javax.annotation.Nonnull;

public class WallClimbPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final Config<WallClimbConfig> config;
    private static WallClimbPlugin instance;
    private ComponentType<EntityStore, WallClimbComponent> wallClimbComponentType;

    public WallClimbPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Initializing " + getName() + " version " + getManifest().getVersion().toString());

        config = withConfig("WallClimbing", WallClimbConfig.CODEC);
        instance = this;
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + getName());

        config.save();

        getCommandRegistry().registerCommand(new WallClimbCommand(config));

        wallClimbComponentType = getEntityStoreRegistry()
                .registerComponent(WallClimbComponent.class, WallClimbComponent::new);
    }

    @Override
    protected void start() {
        getEntityStoreRegistry().registerSystem(
                new WallClimbSystems.WallClimbHolderSystem(wallClimbComponentType));
        getEntityStoreRegistry().registerSystem(
                new WallClimbSystems.WallClimbRefSystem(wallClimbComponentType));
        getEntityStoreRegistry().registerSystem(
                new WallClimbSystems.WallClimbTickSystem(wallClimbComponentType, config));
    }

    @Nonnull
    public ComponentType<EntityStore, WallClimbComponent> getWallClimbComponentType() {
        return wallClimbComponentType;
    }

    @Nonnull
    public static WallClimbPlugin get() {
        return instance;
    }

    public static HytaleLogger getHytaleLogger() {
        return LOGGER;
    }
}
