package dev.lionk;

import de.lioncraft.lionapi.velocity.connections.ConnectionManager;
import de.lioncraft.simpleLobby.velocity.VelocityDataStorage;
import de.lioncraft.simpleLobby.velocity.to.ServerState;
import dev.lionk.data.Config;
import dev.lionk.data.Items;
import dev.lionk.listener.PlayerListeners;
import dev.lionk.listener.WorldListeners;
import dev.lionk.menus.MenuManager;
import dev.lionk.utils.WorldGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerLoadedEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.SchedulerManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main {
    static void main() {
        if(saveResourceIfNotExists("/config.json", Paths.get("config.json"))){
            System.out.println("Config.json has been saved!");
            System.out.println("Please configure the Server now and start the server again!");
        }else{
            Config.init();
            if (!Config.validate()){
                Audiences.console().sendMessage(Component.text("Your config file contains issues. Please fill in everything correctly before starting!", TextColor.color(255, 128, 0)));
                return;
            }

            MinecraftServer server = MinecraftServer.init(new Auth.Velocity(Config.getConfig().getVelocitySecret()));
            System.out.println("Server - Starting");

            InstanceManager instanceManager = MinecraftServer.getInstanceManager();
            InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
            container = instanceContainer;


            // Set the ChunkGenerator
            instanceContainer.setGenerator(new WorldGenerator());
            instanceContainer.setChunkLoader(new AnvilLoader(Paths.get("world/")));
            instanceContainer.setChunkSupplier(LightingChunk::new);

            // Add an event callback to specify the spawning instance (and the spawn position)
            GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
            globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
                final Player player = event.getPlayer();
                event.setSpawningInstance(instanceContainer);
                player.setRespawnPoint(new Pos(0, 67, 0));
                player.setGameMode(GameMode.ADVENTURE);
                player.getInventory().setItemStack(2, Items.getItem("server-selector"));

            });
            globalEventHandler.addListener(PlayerLoadedEvent.class, event -> {
                final Player player = event.getPlayer();
                player.setAllowFlying(true);
                player.getInventory().setItemStack(2, Items.getItem("server-selector"));
            });

            //Register Listeners
            PlayerListeners.register();
            WorldListeners.register();

            MenuManager.registerAll();

            ConnectionManager.initialize(Config.getConfig().getVelocityConnectionSetup());
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.schedule(() -> {
                if (ConnectionManager.isConnectedToVelocity()) {
                    ConnectionManager.getConnectionToVelocity().registerMessageListener("LionLobby_ServerState", (transferrableObject, abstractConnection) -> {
                        ServerState s = ServerState.createServerState(transferrableObject);
                        VelocityDataStorage.addNewServer(s);
                    });
                    VelocityDataStorage.reload();
                }
            }, 3, TimeUnit.SECONDS);

            server.start("127.0.0.1", Config.getConfig().getPort());
            System.out.println("Server - Startup Complete");
        }
    }

    private static InstanceContainer container;

    public static InstanceContainer getContainer() {
        return container;
    }

    private static boolean saveResourceIfNotExists(String resource, Path outputPath) {
        if (resource == null || !resource.startsWith("/")) {
            System.err.println("Error: Resource path must be a non-empty absolute path starting with '/' (e.g., /com/example/file.txt).");
            return false;
        }

        File targetFile = outputPath.toFile();

        if (targetFile.exists()) {
            if (targetFile.isDirectory()) {
                return false;
            }
            return false;
        }
        try {
            InputStream resourceStream = Main.class.getResourceAsStream(resource);
            {
                if (resourceStream == null) {
                    return false;
                }
                Path parentDir = outputPath.getParent();
                if (parentDir != null) {
                    if (!Files.exists(parentDir)) {
                        Files.createDirectories(parentDir);
                    }
                }

                Files.copy(resourceStream, outputPath);
                return true;
            }
        }catch(IOException e) {
            System.err.println("Error saving resource '" + resource + "' to '" + outputPath + "': " + e.getMessage());
            return false;
        } catch (SecurityException e) {
            System.err.println("Error creating directories for '" + outputPath + "' due to security restrictions: " + e.getMessage());
            return false;
        }
    }

    private static final Logger logger = Logger.getLogger("LionLobby");
    public static Logger getLogger() {
        return logger;
    }
}
