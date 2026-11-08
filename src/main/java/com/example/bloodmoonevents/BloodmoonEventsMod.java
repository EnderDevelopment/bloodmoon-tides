package com.example.bloodmoonevents;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

public final class BloodmoonEventsMod implements ModInitializer {
    public static final String MOD_ID = "bloodmoon_events";

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(BloodmoonServerEvents::onServerTick);
    }

    public static boolean isBloodmoon(long dayTime) {
        long day = Math.floorDiv(dayTime, 24000L);
        long timeOfDay = Math.floorMod(dayTime, 24000L);
        return day % 8L == 0L && timeOfDay >= 13000L && timeOfDay < 23000L;
    }
}
