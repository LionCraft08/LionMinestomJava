package dev.lionk.displays;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;

import java.text.AttributedCharacterIterator;

public abstract class AbstractDisplay {
    private LargeGraphics2DFramebuffer framebuffer;
    private Pos topLeft;
    private BlockFace blockFace;

    public AbstractDisplay() {

    }

    /**
     * Sends the content of a LargeGraphics2DFramebuffer to a specific player
     * by rendering it onto a Minecraft map item.
     */
    public void sendFramebufferToPlayer(Player player, LargeGraphics2DFramebuffer framebuffer, int mapId) {
//        // 1. Get the map's byte data from the framebuffer
//        // This is an array of 128x128 byte chunks, where each byte
//        // represents a color index on the map.
//        byte[] mapData = framebuffer.getRenderer();
//
//        // 2. Determine the number of chunks (tiles) in the framebuffer
//        // A LargeGraphics2DFramebuffer is made of 128x128 pixel tiles.
//        int tilesX = framebuffer.createSubView().getTileCountX();
//        int tilesY = framebuffer.getTileCountY();
//
//        // 3. Loop through all tiles and send a MapDataPacket for each.
//        // Each MapDataPacket updates a 128x128 section of a map.
//        for (int tileY = 0; tileY < tilesY; tileY++) {
//            for (int tileX = 0; tileX < tilesX; tileX++) {
//                // Calculate the 128x128 chunk index
//                int chunkIndex = tileY * tilesX + tileX;
//
//                // Extract the 128x128 byte chunk for the current tile
//                int chunkLength = 128 * 128;
//                byte[] chunkData = new byte[chunkLength];
//                System.arraycopy(mapData, chunkIndex * chunkLength, chunkData, 0, chunkLength);
//
//                // Create the MapDataPacket
//                MapDataPacket mapDataPacket = framebuffer.preparePacket(mapId+chunkIndex,
//                        tilesX,
//                        tilesY);/*new MapDataPacket(
//                        mapId + chunkIndex, // The map ID for this tile (must be unique per tile)
//                        (byte) 0,           // Scale (0 is 1:1, full map resolution)
//                        false,              // Locked (usually false for dynamic content)
//                        false,              // Has icons
//                        null,               // Icons (none in this example)
//                        (byte) 128,         // Column count (width of the update)
//                        (byte) 128,         // Row count (height of the update)
//                        (byte) 0,           // X offset
//                        (byte) 0,           // Z offset
//                        chunkData           // The color data for the 128x128 chunk
//                );**/
//
//                // 4. Send the packet to the player
//                player.sendPacket(mapDataPacket);
//            }
//        }

        // 5. Optionally, give the player the map item to view the result
        // The player needs a map item with the *first* map ID (mapId) in their inventory
        // to view the entire composite image. You would typically send multiple
        // MapDataPackets with sequential IDs and give the player the starting Map ID.
        ItemStack mapItem = ItemStack.builder(Material.FILLED_MAP)
                .set(DataComponents.MAP_ID, mapId)
                .build();

        // Check if the player has space, and if not, drop it near them.
        if (!player.getInventory().addItemStack(mapItem)) {
            player.dropItem(mapItem);
        }
    }

}
