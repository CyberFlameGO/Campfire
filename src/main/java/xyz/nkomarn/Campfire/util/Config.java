package xyz.nkomarn.Campfire.util;

import xyz.nkomarn.Campfire.Campfire;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class used to easily interface
 * with the configuration from any class.
 */
public class Config {
    /**
     * Fetches a boolean from the configuration
     * @param location Configuration location of the boolean
     */
    public static boolean getBoolean(String location) {
        try {
            return Campfire.getCampfire().getConfig().getBoolean(location);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches a string from the configuration
     * @param location Configuration location of the string
     */
    public static String getString(String location) {
        try {
            return Campfire.getCampfire().getConfig().getString(location);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
    }

    /**
     * Fetches an integer from the configuration
     * @param location Configuration location of the integer
     */
    public static int getInteger(String location) {
        try {
            return Campfire.getCampfire().getConfig().getInt(location);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public static double getDouble(String location) {
        try {
            return Double.parseDouble(Campfire.getCampfire().getConfig().getString(location));
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Fetches a double from the configuration
     * @param location Configuration location of the double
     */
    public static List<String> getList(String location) {
        try {
            return Campfire.getCampfire().getConfig().getStringList(location);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }
    }
}
