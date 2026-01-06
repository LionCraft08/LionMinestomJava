package de.lioncraft.lionapi.velocity;


import com.google.gson.JsonObject;
import dev.lionk.Main;
import net.kyori.adventure.text.Component;

import java.util.HashMap;

public class CommunicationSettings {
    private final HashMap<String, Boolean> bool = new HashMap<>();
    private final HashMap<String, Integer> integer = new HashMap<>();
    private final HashMap<String, String> strings = new HashMap<>();
    public CommunicationSettings(JsonObject config){
        String s = "settings.velocity-communication.";
        bool.put("disabled", false);
        integer.put("port", config.get("port").getAsInt());
        if (integer.get("port")>65535){
            Main.getLogger().info("The Port provided in config.json/velocity-communication.custom-port is invalid, changing to default");
            integer.put("port", -1);
        }
    }
    public Integer getCustomPort(){
        if (integer.get("port")>65535){
            throw new RuntimeException("Port "+integer.get("port") + " is too high");
        }
        if (integer.get("port")<0){
            return null;
        }
        return integer.get("port");
    }

    public boolean usePortCommunication() {
        return getCustomPort() != null;
    }
}
