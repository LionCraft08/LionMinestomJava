package de.lioncraft.simpleLobby.velocity.to;

import com.google.gson.Gson;
import de.lioncraft.lionapi.velocity.data.TransferrableObject;
import dev.lionk.Main;
import dev.lionk.data.Items;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class ServerState {
    private static final BufferedImage defaultImage = readDefaultImage();
    private static BufferedImage readDefaultImage(){
        try {
            return ImageIO.read(Main.class.getResourceAsStream("/unknown_server.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static final Gson gson = new Gson();
    private String name;
    private boolean enabled;
    private String motd;
    private String base64Favicon;
    private transient BufferedImage decodedBase64String;
    private transient ItemStack itemStack;
    private String stringItemStack;
    private int maxPlayers, currentPlayers;
    private String jsonItemStack;

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return enabled;
    }

    public Component getMOTD() {
        if (motd == null) return Component.text("");
        return JSONComponentSerializer.json().deserialize(motd);
    }

    public String getBase64Favicon() {
        if (base64Favicon.contains("data:image/png;base64,"))
            base64Favicon = base64Favicon.replace("data:image/png;base64,", "");
        return base64Favicon;
    }

    public String getJsonItemStack() {
        return jsonItemStack;
    }
    private JsonServerItemStack jsonItemStackObject;
    public JsonServerItemStack getJsonItemStackObject(){
        if (jsonItemStackObject == null){
            jsonItemStackObject = JsonServerItemStack.deserialize(jsonItemStack);
        }
        return jsonItemStackObject;
    }

    public BufferedImage getFavicon() {
        if (decodedBase64String == null)
            createFavicon();
        return decodedBase64String;
    }

    private void createFavicon(){
        if (base64Favicon == null || base64Favicon.isBlank()){
            decodedBase64String = defaultImage;
            return;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(getBase64Favicon());
        try {
            decodedBase64String = ImageIO.read(new ByteArrayInputStream(decodedBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack getItemStack() {
        if (itemStack == null){
            ItemStack is = ItemManager.createJsonItem(getJsonItemStackObject());
            if (is == null){
                itemStack = getDefaultItemStack();
            }else itemStack = Items.createClickItem(is, "lionlobby_server_connect:" + name);
        }
        return itemStack;
    }

    public ItemStack getDefaultItemStack(){
        ItemStack is = ItemStack.of(Material.REDSTONE);
        return is.withCustomName(Component.text(getName()))
                .withLore(List.of(Component.text("Joins this Server.")));
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void setMotd(String motd) {
        this.motd = motd;
    }

    private void setBase64Favicon(String base64Favicon) {
        this.base64Favicon = base64Favicon;
    }

    private void setDecodedBase64String(BufferedImage decodedBase64String) {
        this.decodedBase64String = decodedBase64String;
    }

    private void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    private void setCurrentPlayers(int currentPlayers) {
        this.currentPlayers = currentPlayers;
    }

    public static ServerState createServerState(TransferrableObject to){
        if (Objects.equals(to.getObjectType(), "LionLobby_ServerState")){
            return createServerState(to.getString("data"));
        } else throw new MalformedParametersException("The provided object is not a ServerState object and can't be deserialized");
    }
    public static ServerState createServerState(String json){
        return gson.fromJson(json, ServerState.class);
    }
}
