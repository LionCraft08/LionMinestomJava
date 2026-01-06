package de.lioncraft.lionapi.velocity;


import de.lioncraft.lionapi.velocity.connections.AbstractConnection;
import de.lioncraft.lionapi.velocity.connections.DirectConnection;
import dev.lionk.Main;

import java.util.HashMap;

public class CommunicationManager {
    private static HashMap<String, AbstractConnection> queuedConnections = new HashMap<>();
    private static HashMap<String, AbstractConnection> connections = new HashMap<>();

    public static void registerConnection(DirectConnection c){
        Main.getLogger().info("Enabling Connection to "+c.getName()+" ("+c.getHost()+":"+c.getPort()+")");

    }

    static AbstractConnection getConnection(String name){
        return connections.get(name);
    }

}
