package net.mat0u5.morph.utils;

import static net.mat0u5.morph.Main.MOD_VERSION;

public class VersionControl {
    public static boolean isDevVersion() {
        return MOD_VERSION.contains("dev");
    }

    public static int getModVersionInt(String string) {
        if (string.startsWith("v.")) {
            string = string.substring(2);
        }
        string = string.replaceAll("^\\D+", "");

        String[] parts = string.split("\\.");

        int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
        int build = parts.length > 3 ? Integer.parseInt(parts[3]) : 0;

        return (major * 100000) + (minor * 10000) + (patch * 1000) + build;
    }

    /*
     *     COMPATIBILITY TABLE
     *   0.0.1      -   *
     */

    public static String clientCmpatibilityMin() {
        //This is the version that the SERVER needs to have for the current client.
        return "dev-0.0.1";
    }

    public static String serverCompatibilityMin() {
        //This is the version that the CLIENT needs to have for the current server.
        return "dev-0.0.1";
    }
}
