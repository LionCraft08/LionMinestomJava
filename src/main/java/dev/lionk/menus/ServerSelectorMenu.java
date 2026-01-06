package dev.lionk.menus;

import de.lioncraft.simpleLobby.velocity.VelocityDataStorage;
import de.lioncraft.simpleLobby.velocity.to.ServerState;
import dev.lionk.data.Items;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

import java.awt.*;

public class ServerSelectorMenu extends Inventory {
    public ServerSelectorMenu() {
        super(InventoryType.CHEST_6_ROW, Component.text("Select a Server.", NamedTextColor.DARK_AQUA));
        for (int i = 0;i<54;i++){
            super.setItemStack(i, Items.getItem("block"));
        }
        int i = 9;
        for (String s : VelocityDataStorage.getStoredServers().keySet()) {
            if (i >= 45) return;
            super.setItemStack(i, VelocityDataStorage.getStoredServers().get(s).getItemStack());
            i++;
        }
    }

}
