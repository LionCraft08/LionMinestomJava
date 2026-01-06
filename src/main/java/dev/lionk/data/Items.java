package dev.lionk.data;

import de.lioncraft.simpleLobby.velocity.VelocityCommunication;
import dev.lionk.menus.MenuManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.item.component.TooltipDisplay;
import net.minestom.server.tag.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Items {
    private static final HashMap<String, ItemStack> items = new HashMap<String, ItemStack>(
            Map.of("server-selector", createClickItem(ItemStack.builder(Material.COMPASS)
                    .set(DataComponents.CUSTOM_NAME,
                            Component.text("Server Selector",
                                    TextColor.color(0, 244, 255)))
                                .set(DataComponents.LORE, List.of(Component.text("Interact to select a Server."))).build(),
                            "open_menu:server-selector"),
                    "menu", ItemStack.builder(Material.REDSTONE_TORCH)
                            .set(DataComponents.CUSTOM_NAME,
                                    Component.text("Menu", TextColor.color(200, 0, 255))).build(),
                    "block",
                    createClickItem(ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE).
                            set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.EMPTY.withHideTooltip(true)).build(), "block:true"))
    );

    public static ItemStack createClickItem(ItemStack itemStack, String command) {
        return itemStack.with(DataComponents.CUSTOM_DATA, CustomData.EMPTY.withTag(Tag.String("lion_command"), command));
    }

    public static ItemStack getItem(String id){
        return items.get(id);
    }

    public static void onClick(String id, Player player, Click clickType){
        String value = getValue(id);
        switch(getKey(id)){
            case "open_menu" ->{
                MenuManager.openMenu(value, player);
            }
            case "block" -> {
                if (Boolean.parseBoolean(value)){
                    player.playSound(Sound.sound(Key.key("entity.silverfish.ambient"), Sound.Source.UI, 0.5f, 0.8f));
                }
            }
            case "lionlobby_server_connect" ->{
                VelocityCommunication.sendServerSendRequest(player, value);
                player.closeInventory();
            }
        }
    }
    private static String getValue(String id){
        if(id.contains(":")){
            return id.substring(id.indexOf(":")+1);
        }else return id;
    }
    private static String getKey(String id){
        if(id.contains(":")){
            return id.substring(0, id.indexOf(":"));
        }else return id;
    }

}
