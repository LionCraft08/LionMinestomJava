package de.lioncraft.lionapi.velocity.data;

import de.lioncraft.lionapi.velocity.connections.AbstractConnection;

public interface IncomingMessageListener {
    void onReceive(TransferrableObject tf, AbstractConnection connection);
}
