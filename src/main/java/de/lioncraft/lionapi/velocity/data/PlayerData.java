package de.lioncraft.lionapi.velocity.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minestom.server.MinecraftServer;

import java.util.HashMap;
import java.util.UUID;

public class PlayerData {
    private static final Gson gson = new Gson();
    private UUID uuid;
    private String name;
    private boolean isOP;
    private Long lastOnline;
    private HashMap<String, JsonElement> data = new HashMap<>();

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isOP() {
        return isOP;
    }

    public Long getLastOnline() {
        return lastOnline;
    }

    public HashMap<String, JsonElement> getData() {
        return data;
    }

    public String toString(){
        return gson.toJson(this);
    }

    public void setOP(boolean OP) {
        isOP = OP;
    }

    public static PlayerData fromString(String jsonString){
        return gson.fromJson(jsonString, PlayerData.class);
    }


}
