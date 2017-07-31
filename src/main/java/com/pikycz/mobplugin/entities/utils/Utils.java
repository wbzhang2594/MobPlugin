/**
 * Utils.java
 * <p>
 * Created on 10:18:38
 */
package com.pikycz.mobplugin.entities.utils;

import cn.nukkit.Server;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.utils.TextFormat;

import java.util.Random;

/**
 * @author <a href="mailto:kniffman@googlemail.com">Michael Gertz (kniffo80)</a>
 */
public class Utils {

    private static final Server SERVER = Server.getInstance();

    public static final void logServerInfo(String text) {
        SERVER.getLogger().info(TextFormat.GOLD + "[MobPlugin] " + text);
    }

    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * Returns a random number between min (inkl.) and max (excl.) If you want a
     * number between 1 and 4 (inkl) you need to call rand (1, 5)
     *
     * @param min min inklusive value
     * @param max max exclusive value
     * @return
     */
    public static int rand(int min, int max) {
        if (min == max) {
            return max;
        }
        return min + random.nextInt(max - min);
    }

    /**
     * Returns random boolean
     *
     * @return a boolean random value either <code>true</code> or
     * <code>false</code>
     */
    public static boolean rand() {
        return random.nextBoolean();
    }

    public static int roundUp(int number, int interval) {
        if (interval == 0) {
            return 0;
        } else if (number == 0) {
            return interval;
        } else {
            if (number < 0) {
                interval *= -1;
            }

            int i = number % interval;
            return i == 0 ? number : number + interval - i;
        }
    }

    public static int getHIghestFilledChunkSection(Chunk chunk) {
        int lastIndex = 0;

        for (int i = 0; i < chunk.getSections().length; i++) {
            if (chunk.getSection((float) i).isEmpty()) {
                return i;
            }

            lastIndex = i;
        }

        return lastIndex;
    }

}
