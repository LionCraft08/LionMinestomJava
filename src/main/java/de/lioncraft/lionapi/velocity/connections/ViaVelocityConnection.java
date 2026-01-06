package de.lioncraft.lionapi.velocity.connections;

import de.lioncraft.lionapi.velocity.data.TransferrableObject;

public class ViaVelocityConnection extends AbstractConnection {
    public ViaVelocityConnection(String name) {
        super(name);

    }

    @Override
    public boolean sendMessage(TransferrableObject message) {
        return true;
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public boolean isConnected(){
        return false;
        //TODO!!!
    }
}
