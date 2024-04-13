package com.armorhud.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AllowedPlayers {
    private static final Set<String> allowedUUIDs = new HashSet<>();
    private static final Set<String> allowedNames = new HashSet<>();

    static {
        loadAllowedUUIDs("https://raw.githubusercontent.com/DaRomanLove/Hwid/main/alloweduuids.json?hwid=");
        loadAllowedNames("https://raw.githubusercontent.com/DaRomanLove/Hwid/main/allowednames.json?hwid=");
    }

    private static void loadAllowedUUIDs(String url) {
        try {
            URL resourceUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream()));
            String uuid;
            while ((uuid = reader.readLine()) != null) {
                allowedUUIDs.add(uuid.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadAllowedNames(String url) {
        try {
            URL resourceUrl = new URL(url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream()));
            String name;
            while ((name = reader.readLine()) != null) {
                allowedNames.add(name.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isPlayerAllowedByUUID(UUID playerUUID) {
        return allowedUUIDs.contains(playerUUID.toString());
    }

    public static boolean isPlayerAllowedByName(String playerName) {
        return allowedNames.contains(playerName);
    }
}
