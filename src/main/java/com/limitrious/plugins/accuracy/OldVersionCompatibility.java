package com.darkender.plugins.accuracy;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class OldVersionCompatibility {
    static Vector getBlockFaceDirection(BlockFace face) {
        switch (face) {
            case NORTH: return new Vector(0, 0, -1);
            case EAST: return new Vector(1, 0, 0);
            case SOUTH: return new Vector(0, 0, 1);
            case WEST: return new Vector(-1, 0, 0);
            case UP: return new Vector(0, 1, 0);
            case DOWN: return new Vector(0, -1, 0);
        }
        return null;
    }

    public static Vector rotateAroundNonUnitAxis(Vector vector, Vector axis, double angle) {
        double x = vector.getX(), y = vector.getY(), z = vector.getZ();
        double x2 = axis.getX(), y2 = axis.getY(), z2 = axis.getZ();

        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = vector.dot(axis);

        return vector.setX(
            x2 * dotProduct * (1 - cosTheta) + x * cosTheta + (-z2 * y + y2 * z) * sinTheta
        ).setY(
            y2 * dotProduct * (1 - cosTheta) + y * cosTheta + (z2 * x - x2 * z) * sinTheta
        ).setZ(
            z2 * dotProduct * (1 - cosTheta) + z * cosTheta + (-y2 * x + x2 * y) * sinTheta
        );
    }
}
