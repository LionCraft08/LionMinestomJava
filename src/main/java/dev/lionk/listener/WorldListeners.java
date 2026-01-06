package dev.lionk.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerBlockBreakEvent;

public class WorldListeners {
    public static void register(){
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class,
                e -> {
                    e.setCancelled(true);
                });
    }
}
