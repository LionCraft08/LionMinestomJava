package de.lioncraft.simpleLobby.velocity;

import de.lioncraft.lionapi.velocity.connections.ConnectionManager;
import de.lioncraft.lionapi.velocity.data.TransferrableObject;
import de.lioncraft.simpleLobby.velocity.to.ServerState;

import java.util.HashMap;

public final class VelocityDataStorage {
    private static final HashMap<String, ServerState> storedServers = new HashMap<>();

    public static void reload(){
        if(ConnectionManager.isConnectedToVelocity()){
            ConnectionManager.getConnectionToVelocity().sendMessage(new TransferrableObject("LionLobby_RequestServerStates"));
        }
    }

    public static HashMap<String, ServerState> getStoredServers(){
        return storedServers;
    }

    public static void addNewServer(ServerState serverState){
        storedServers.put(serverState.getName(), serverState);
    }
}
