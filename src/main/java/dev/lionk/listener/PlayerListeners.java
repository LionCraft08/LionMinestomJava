package dev.lionk.listener;

import dev.lionk.Main;
import dev.lionk.data.Items;
import dev.lionk.features.DoubleJump;
import dev.lionk.menus.MenuManager;
import dev.lionk.menus.ServerSelectorMenu;
import net.minestom.server.MinecraftServer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerStartFlyingEvent;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;

import java.util.Objects;

public class PlayerListeners {
    public static void register(){
        MinecraftServer.getGlobalEventHandler().addListener(PlayerUseItemEvent.class, playerUseItemEvent -> {
            playerUseItemEvent.setCancelled(true);
            if (playerUseItemEvent.getItemStack().get(DataComponents.CUSTOM_DATA) != null){
                String s = Objects.requireNonNull(playerUseItemEvent.getItemStack().get(DataComponents.CUSTOM_DATA)).getTag(Tag.String("lion_command"));
                if (s!=null&&s.startsWith("open_menu")){
                    MenuManager.openMenu(s.substring(s.indexOf(":")+1), playerUseItemEvent.getPlayer());
                }
            }
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSwapItemEvent.class, e -> {
            e.setCancelled(true);
        });
        MinecraftServer.getGlobalEventHandler().addListener(ItemDropEvent.class, e -> {
            e.setCancelled(true);
        });
        MinecraftServer.getGlobalEventHandler().addListener(InventoryPreClickEvent.class, e -> {
            e.setCancelled(true);
            if (e.getClickedItem().get(DataComponents.CUSTOM_DATA) != null){
                Items.onClick(e.getClickedItem().get(DataComponents.CUSTOM_DATA).getTag(Tag.String("lion_command")),
                        e.getPlayer(),
                        e.getClick());
            }
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerStartFlyingEvent.class, e -> {
            DoubleJump.onDoubleJump(e.getPlayer());
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerMoveEvent.class, e -> {
            if (isNearGround(e.getPlayer())) {
                e.getPlayer().setAllowFlying(true);
            }
        });

    }
    private static boolean isNearGround(Player player) {
        Pos loc = player.getPosition().sub(0, 0.1, 0);
        Block block = Main.getContainer().getBlock(loc);

        // Check if the block is a solid, non-air block.
        // We also check for lava/water to prevent jumping out of liquids.
        return block.isSolid() &&
                !block.compare(Block.AIR) &&
                !block.isLiquid();
    }
}
