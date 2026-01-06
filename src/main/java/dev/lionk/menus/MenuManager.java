package dev.lionk.menus;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class MenuManager {
    private static final HashMap<String, Class<? extends Inventory>> MENU = new HashMap<>();
    public static Class<? extends Inventory> getMenu(String id) {
        return MENU.get(id);
    }
    public static void openMenu(String id, Player player) {
        Class<? extends Inventory> inventory = MENU.get(id);
        if (inventory != null) {
            try {
                Inventory inv = inventory.getConstructor().newInstance();
                player.openInventory(inv);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        } else System.out.println("Menu not found: "+id);

    }
    public static void registerMenu(String id, Class<? extends Inventory> inventory) {
        MENU.put(id, inventory);
    }

    public static void registerAll(){
        registerMenu("server-selector", ServerSelectorMenu.class);
    }
}
