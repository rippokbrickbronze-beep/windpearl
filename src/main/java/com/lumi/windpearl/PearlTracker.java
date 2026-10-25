package com.lumi.windpearl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Remembers where our own pearls are going, because by the time the wind charge
 * arrives the pearl has moved. Tracks velocity, not just position.
 */
public final class PearlTracker {
    public static final class Track {
        public UUID id;
        public Vec3d pos;
        public Vec3d vel;
        public int ageTicks;
        public long lastSeen;
    }

    private final Map<UUID, Track> tracks = new HashMap<>();

    public void tick(MinecraftClient mc) {
        ClientWorld world = mc.world;
        long now = System.currentTimeMillis();
        if (world == null || mc.player == null) {
            tracks.clear();
            return;
        }

        for (Entity e : world.getEntities()) {
            if (!(e instanceof EnderPearlEntity pearl)) continue;
            if (pearl.getOwner() != mc.player) continue;

            Track t = tracks.computeIfAbsent(pearl.getUuid(), k -> new Track());
            t.id = pearl.getUuid();
            t.pos = new Vec3d(pearl.getX(), pearl.getY(), pearl.getZ());
            t.vel = pearl.getVelocity();
            t.ageTicks++;
            t.lastSeen = now;
        }

        // drop anything we have not seen for half a second
        tracks.values().removeIf(t -> now - t.lastSeen > 500L);
    }

    public Track nearest(Vec3d from, double maxDist) {
        Track best = null;
        double bestSq = maxDist * maxDist;
        for (Track t : tracks.values()) {
            double d = t.pos.squaredDistanceTo(from);
            if (d < bestSq) {
                bestSq = d;
                best = t;
            }
        }
        return best;
    }

    public Track newest() {
        Track best = null;
        for (Track t : tracks.values()) {
            if (best == null || t.ageTicks < best.ageTicks) best = t;
        }
        return best;
    }

    public void clear() {
        tracks.clear();
    }

    public int size() {
        return tracks.size();
    }
}
