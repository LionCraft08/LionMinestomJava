package de.lioncraft.simpleLobby.velocity.to;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;

public class JsonServerItemStack {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String name;
    private String material;
    private String[] description;

    public String getName() {
        return name;
    }

    public String getMaterial() {
        return material.toLowerCase();
    }

    public String[] getDescription() {
        return description;
    }

    public Component getComponentName(){
        return MiniMessage.miniMessage().deserialize(getName());
    }

    public List<Component> getComponentDescription(){
        List<Component> list = new ArrayList<>();
        for(String s : description){
            list.add(MiniMessage.miniMessage().deserialize(s));
        }
        return list;
    }

    public static JsonServerItemStack deserialize(String json){
        return gson.fromJson(json, JsonServerItemStack.class);
    }
}
