package org.example.plugin;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.collision.BlockContactData;
import com.hypixel.hytale.server.core.modules.collision.BlockData;
import com.hypixel.hytale.server.core.modules.collision.IBlockCollisionConsumer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WallClimbingComponent  implements IBlockCollisionConsumer, Component<EntityStore> {
    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }

    @Override
    public Result onCollision(int i, int i1, int i2, Vector3d vector3d, BlockContactData blockContactData, BlockData blockData, Box box) {
        return null;
    }

    @Override
    public Result probeCollisionDamage(int i, int i1, int i2, Vector3d vector3d, BlockContactData blockContactData, BlockData blockData) {
        return null;
    }

    @Override
    public void onCollisionDamage(int i, int i1, int i2, Vector3d vector3d, BlockContactData blockContactData, BlockData blockData) {

    }

    @Override
    public Result onCollisionSliceFinished() {
        return null;
    }

    @Override
    public void onCollisionFinished() {

    }
}
