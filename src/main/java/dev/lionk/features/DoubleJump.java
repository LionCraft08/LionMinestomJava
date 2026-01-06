package dev.lionk.features;


import dev.lionk.data.Config;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Vector;

public final class DoubleJump {
    private static final boolean doubleJumpEnabled = true;
    private static final double doubleJumpUpwards = Config.getConfig().getDoubleJumpConfig().get("velocity").getAsDouble();
    private static final double doubleJumpForwards = Config.getConfig().getDoubleJumpConfig().get("velocityy").getAsDouble();

    /**An internal function to check whether a player acn double jump and
     * perform the double jump when possible.
     * @param p the player to check
     * @return Whether the player double jumped successfully
     */
    public static boolean onDoubleJump(Player p){
        if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return false;
        if (doubleJumpEnabled){
            //If another function requires the player to fly cancel double jump attempt
            //(Plugin only)

            Vec launchVelocity = getLaunchVelocity(p);

            // 4. Apply to the player
            p.setVelocity(launchVelocity);

            p.setFlying(false);
            p.setAllowFlying(false);

            return true;
        }
        return false;
    }

    private static @NonNull Vec getLaunchVelocity(Player p) {
        Pos position = p.getPosition();
        Vec direction = position.direction();

        // 2. Calculate horizontal velocity (X and Z)
        // We multiply the unit vector by our horizontalPower
        double velX = direction.x() * doubleJumpForwards;
        double velZ = direction.z() * doubleJumpForwards;

        // 3. Create the final velocity vector
        // We manually set the Y component to control the "upward" height
        return new Vec(velX, doubleJumpUpwards, velZ);
    }

    /**
     * Whether the double jump is enabled in the settings.
     */
    public static boolean isDoubleJumpEnabled() {
        return doubleJumpEnabled;
    }
}
