package com.example.bloodmoonevents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

final class BloodmoonServerEvents {
    private static final int WATER_DAMAGE_INTERVAL = 10;
    private static final int SPAWN_INTERVAL = 20;
    private static final int SPAWN_RADIUS = 24;
    private static final int MAX_NEARBY_MONSTERS = 32;
    private static final Map<UUID, Integer> waterDamageTimers = new HashMap<>();
    private static int spawnTimer;

    private BloodmoonServerEvents() {
    }

    static void onServerTick(MinecraftServer server) {
        boolean bloodmoon = false;
        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension() == Level.OVERWORLD) {
                bloodmoon = BloodmoonEventsMod.isBloodmoon(level.getDayTime());
                break;
            }
        }

        if (!bloodmoon) {
            waterDamageTimers.clear();
            spawnTimer = 0;
            return;
        }

        damagePlayersInWater(server);
        spawnTimer++;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            spawnNearPlayers(server);
        }
    }

    private static void damagePlayersInWater(MinecraftServer server) {
        Iterator<Map.Entry<UUID, Integer>> iterator = waterDamageTimers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            if (server.getPlayerList().getPlayer(entry.getKey()) == null) {
                iterator.remove();
            }
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            int timer = waterDamageTimers.getOrDefault(player.getUUID(), 0);
            if (player.isInWaterOrBubble()) {
                if (timer <= 0) {
                    player.hurtServer(player.serverLevel(), player.damageSources().drown(), 2.0F);
                    timer = WATER_DAMAGE_INTERVAL;
                } else {
                    timer--;
                }
                waterDamageTimers.put(player.getUUID(), timer);
            } else {
                waterDamageTimers.remove(player.getUUID());
            }
        }
    }

    private static void spawnNearPlayers(MinecraftServer server) {
        RandomSource random = server.overworld().getRandom();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerLevel level = player.serverLevel();
            if (level.dimension() != Level.OVERWORLD || player.isSpectator()) {
                continue;
            }

            AABB nearby = player.getBoundingBox().inflate(SPAWN_RADIUS);
            if (level.getEntitiesOfClass(Monster.class, nearby).size() >= MAX_NEARBY_MONSTERS) {
                continue;
            }

            BlockPos spawnPos = findSpawnPosition(level, player.blockPosition(), random);
            if (spawnPos == null) {
                continue;
            }

            Zombie zombie = EntityType.ZOMBIE.create(level);
            if (zombie == null) {
                continue;
            }
            zombie.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            level.addFreshEntity(zombie);
        }
    }

    private static BlockPos findSpawnPosition(ServerLevel level, BlockPos center, RandomSource random) {
        for (int attempt = 0; attempt < 12; attempt++) {
            int x = center.getX() + Mth.nextInt(random, -SPAWN_RADIUS, SPAWN_RADIUS);
            int z = center.getZ() + Mth.nextInt(random, -SPAWN_RADIUS, SPAWN_RADIUS);
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos position = new BlockPos(x, y, z);
            if (level.getBlockState(position).isAir() && level.getBlockState(position.above()).isAir()
                    && level.getBlockState(position.below()).isSolid()) {
                return position;
            }
        }
        return null;
    }
}
