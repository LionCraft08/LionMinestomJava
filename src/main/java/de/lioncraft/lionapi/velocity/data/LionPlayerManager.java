package de.lioncraft.lionapi.velocity.data;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.UUID;

public class LionPlayerManager {
    private static HashMap<UUID, PlayerData> map = new HashMap<>();

    @ApiStatus.Internal
    public static void addPlayerData(PlayerData playerData){
        map.put(playerData.getUuid(), playerData);
    }

    public static PlayerData getPlayerData(UUID uuid){
        return map.get(uuid);
    }
}
