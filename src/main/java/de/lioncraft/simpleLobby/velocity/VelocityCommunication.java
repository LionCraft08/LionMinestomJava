package de.lioncraft.simpleLobby.velocity;

import de.lioncraft.lionapi.velocity.connections.ConnectionManager;
import de.lioncraft.lionapi.velocity.data.TransferrableObject;
import net.minestom.server.entity.Player;

public class VelocityCommunication {
    public static void sendServerSendRequest(Player p, String server){
        ConnectionManager.getConnectionToVelocity().sendMessage(
                new TransferrableObject("LionLobby_PlayerTransfer")
                        .addValue("player", p.getUuid().toString())
                        .addValue("server", server));
    }
}
