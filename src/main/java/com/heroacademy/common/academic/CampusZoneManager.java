package com.heroacademy.common.academic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class CampusZoneManager {

    /**
     * Retorna a zona mais específica onde as coordenadas se encontram, ou null se estiver fora das zonas demarcadas.
     */
    public static CampusZone getZoneAt(double x, double y, double z) {
        CampusZone bestMatch = null;
        double smallestVolume = Double.MAX_VALUE;

        for (CampusZone zone : CampusZone.values()) {
            if (zone.contains(x, y, z)) {
                AABB b = zone.getBounds();
                double volume = (b.maxX - b.minX) * (b.maxY - b.minY) * (b.maxZ - b.minZ);
                if (volume < smallestVolume) {
                    smallestVolume = volume;
                    bestMatch = zone;
                }
            }
        }

        return bestMatch;
    }

    public static CampusZone getZoneAt(BlockPos pos) {
        return getZoneAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
}
