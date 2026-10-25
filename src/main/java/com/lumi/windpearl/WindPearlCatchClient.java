package com.lumi.windpearl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class WindPearlCatchClient implements ClientModInitializer {
    public static final PearlTracker TRACKER = new PearlTracker();

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(TRACKER::tick);
    }
}
