package de.lioncraft.simpleLobby.velocity.to;

import com.google.gson.JsonObject;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.recipe.display.SlotDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ItemManager {
    private static final Logger log = LoggerFactory.getLogger(ItemManager.class);

    public static ItemStack createItem(String ymlString){
        try {
            return null;
        }catch (Exception e){
            return null;
        }
    }
    public static ItemStack createJsonItem(JsonServerItemStack jsonObject){
        if(Objects.isNull(jsonObject)){
            return null;
        }
        return ItemStack.builder(Objects.requireNonNullElse(Material.fromKey(
                        jsonObject.getMaterial()
        ),
                Material.REDSTONE)).customName(jsonObject.getComponentName())
                .lore(jsonObject.getComponentDescription()).build();
    }
}
