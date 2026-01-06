package de.lioncraft.lionapi.velocity.connections;

import de.lioncraft.lionapi.velocity.data.IncomingMessageListener;
import de.lioncraft.lionapi.velocity.data.TransferrableObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractConnection {
    private String name;
    private boolean isEnabled = false;
    protected HashMap<String, IncomingMessageListener> listeners = new HashMap<>();
    protected List<IncomingMessageListener> permanentListeners = new ArrayList<>();

    public AbstractConnection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * CALL ASYNCHRONOUS!!!
     */
    public void enableConnection(){
        onEnable();
        isEnabled = true;
    }

    void onMessageReceive(String message){
        TransferrableObject to = TransferrableObject.getFromJson(message);
        permanentListeners.forEach(incomingMessageListener -> {
            incomingMessageListener.onReceive(to, this);
        });

        listeners.forEach((s, incomingMessageListener) -> {
            if (s == null || (s.equals(to.getObjectType()))){
                incomingMessageListener.onReceive(to, this);
            }
        });
    }

    /**
     * CALL ASYNCHRONOUS!!!
     */
    public void disableConnection(){
        onDisable();
        isEnabled = false;
    }

    /**
     * Sends a Message to this connection
     * @param message The Message to send
     * @return Whether the message was sent successfully
     */
    public abstract boolean sendMessage(TransferrableObject message);
    public void registerMessageListener(@Nullable String objectType, IncomingMessageListener c){
        if (objectType == null) permanentListeners.add(c);
        else listeners.put(objectType, c);
    }
    protected abstract void onEnable();
    protected abstract void onDisable();
}
