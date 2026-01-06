package dev.lionk.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.lionk.Main;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public final class Config {
    private Config() {}
    private static Config config;
    public static final String VERSION = "1.0.0";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static void init(){
        try {
            config = gson.fromJson(new FileReader("config.json"), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean validate(){
        return (config.port>0&&config.port<=65536)
                &&(!Objects.equals(getConfig().VelocitySecret, "addYourSecretHERE"));
    }
    public static Config getConfig() {
        return config;
    }

    private int port;
    private String VelocitySecret;
    private JsonObject VelocityConnectionSetup;
    private JsonObject doubleJumpConfig;

    public JsonObject getVelocityConnectionSetup() {
        return VelocityConnectionSetup;
    }

    public JsonObject getDoubleJumpConfig() {
        return doubleJumpConfig;
    }

    public int getPort() {
        return port;
    }

    public String getVelocitySecret() {
        return VelocitySecret;
    }
}
