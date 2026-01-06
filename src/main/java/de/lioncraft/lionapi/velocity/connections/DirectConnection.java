package de.lioncraft.lionapi.velocity.connections;

import de.lioncraft.lionapi.velocity.data.TransferrableObject;
import dev.lionk.Main;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * Handles the low-level TCP connection and message handling.
 * This class runs all network I/O operations on a separate thread pool
 * to avoid blocking the main server thread.
 */
public class DirectConnection extends AbstractConnection{
    
    private final String host;
    private final int port;
    private final boolean isServer;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final ExecutorService executorService;
    private volatile boolean isRunning = true;

    public DirectConnection(String host, int port, String name, boolean isServer) {
        super(name);
        this.host = host;
        this.port = port;
        this.isServer = isServer;
        // Use a fixed-size thread pool for network operations
        this.executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Starts the TCP connection, either as a server or a client.
     */
    private void start() {
        if (isServer) {
            startServer();
        } else {
            startClient();
        }
    }

    /**
     * Starts the TCP server to listen for an incoming client connection.
     */
    private void startServer() {
        executorService.submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(5000);
                Main.getLogger().info("TCP Server started, waiting for client connection on port " + port + "...");

                // This will block until a client connects
                clientSocket = serverSocket.accept();
                Main.getLogger().info("Client connected from " + clientSocket.getInetAddress().getHostAddress());

                setupStreams();
                readMessages(); // Start a new thread for reading incoming messages
                sendMessage("setupconnection:"+MinecraftServer.getServer().getAddress()+":"+MinecraftServer.getServer().getPort());

            } catch (IOException e) {
                Main.getLogger().log(Level.SEVERE, "Server failed to start or accept connection.", e);
            }
        });
    }

    /**
     * Starts the TCP client to connect to the server.
     */
    private void startClient() {
        executorService.submit(() -> {
            try {
                Main.getLogger().info("Attempting to connect to TCP server at " + host + ":" + port + "...");
                clientSocket = new Socket(host, port);
                Main.getLogger().info("Successfully connected to the server!");

                setupStreams();
                readMessages(); // Start a new thread for reading incoming messages

                ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
                scheduledExecutor.schedule(() -> {
                    sendMessage("setupconnection:"+MinecraftServer.getServer().getAddress()+":"+MinecraftServer.getServer().getPort());
                    Main.getLogger().info("Sent connection setup");
                }, 1, TimeUnit.SECONDS);

            } catch (IOException e) {
                Main.getLogger().log(Level.SEVERE, "Client failed to connect to server.");
            }
        });
    }

    /**
     * Sets up the input and output streams for the socket.
     */
    private void setupStreams() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /**
     * Starts a new thread to continuously read messages from the input stream.
     * This method is called after a connection is established.
     */
    private void readMessages() {
        executorService.submit(() -> {
            Main.getLogger().info("Started listening for messages");
            String receivedLine;
            try {
                while (isRunning && (receivedLine = in.readLine()) != null) {
                    // Call the callback method on the main Main class
                    onMessageReceive(receivedLine);
                }
            } catch (IOException e) {
                if (isRunning && MinecraftServer.getServer().isOpen()) { // Only log if not a planned shutdown
                    Main.getLogger().log(Level.WARNING, "Connection lost with peer.", e);
                }
            } finally {
                shutdown();
            }
        });
    }

    /**
     * Asynchronously sends a message to the connected socket.
     * @param message The message to send.
     */
    private void sendMessage(String message) {
        executorService.submit(() -> {
            if (out != null) {
                out.println(message);
            } else {
                Main.getLogger().warning("Cannot send message. Connection is not active.");
            }
        });
    }

    /**
     * Shuts down all connections and the thread pool.
     */
    private void shutdown() {
        this.isRunning = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            Main.getLogger().log(Level.SEVERE, "Error while closing network connections.", e);
        } finally {
            if (!executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }
    }

    public boolean isConnected() {
        return clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected();
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public boolean isServer() {
        return isServer;
    }

    @Override
    public boolean sendMessage(TransferrableObject message) {
        if (isConnected()){
            sendMessage(message.toString());
            return true;
        }
        return false;
    }

    @Override
    protected void onEnable() {
        start();
    }

    @Override
    protected void onDisable() {
        shutdown();
    }
}

