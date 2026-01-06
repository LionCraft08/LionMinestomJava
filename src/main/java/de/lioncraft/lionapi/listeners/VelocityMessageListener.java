package de.lioncraft.lionapi.listeners;

import de.lioncraft.lionapi.velocity.data.LionPlayerManager;
import de.lioncraft.lionapi.velocity.data.PlayerData;
import de.lioncraft.lionapi.velocity.data.TransferrableObject;
import dev.lionk.Main;
import net.minestom.server.MinecraftServer;

import java.util.UUID;

public final class VelocityMessageListener {
    public static void onReceive(TransferrableObject to){
        switch (to.getObjectType()){
            case "lionapi_playerdata" -> {
                LionPlayerManager.addPlayerData(PlayerData.fromString(to.getString("data")));
            }
            case "lionapi_playerdata_update" -> {
                PlayerData pd = LionPlayerManager.getPlayerData(UUID.fromString(to.getString("player")));
                if (pd == null) return;
                switch (to.getString("key")){
                    case "isOP" -> {
                        pd.setOP(to.getBool("data"));
                    }
                }
            }
            case "lionapi_shutdown" -> {
                Main.getLogger().info("Attempting to shut down");
                MinecraftServer.stopCleanly();
            }
        }
    }
}
