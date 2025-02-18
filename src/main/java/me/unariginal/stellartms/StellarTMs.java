package me.unariginal.stellartms;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StellarTMs implements ModInitializer {
    private final String MOD_ID = "stellartms";
    public final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static StellarTMs instance;
    public MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.server = server);
    }
}
