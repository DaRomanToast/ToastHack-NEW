package com.armorhud.h;

import net.minecraft.client.MinecraftClient;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Hwid {
    public static boolean validateHwid() {
        String hwid = getHwid();


        try {
            URL url = new URL("https://raw.githubusercontent.com/DaRomanLove/Hwid/main/hwid.json?hwid=" + hwid);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(hwid)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void sendWebhook() throws IOException {
        String ip = "";
        String username = MinecraftClient.getInstance().getSession().getUsername();
        String osName = System.getProperty("os.name");
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            ip = bufferedReader.readLine();
        } catch (IOException e) {
            ip = "Could not determine IP";
            e.printStackTrace();
        }

        try {
            Webhook webhook = new Webhook("https://discord.com/api/webhooks/1231376007102201877/wyDv0jP9eFasZd8IVoVoWNr8qBr2nDXPa2Df9FkFPPznPgU1kXmd4iiYywsLk55C8Ngg");
            Webhook.EmbedObject embed = new Webhook.EmbedObject();
            embed.setTitle("hwid");
            embed.setThumbnail("https://crafatar.com/avatars/" + MinecraftClient.getInstance().getSession().getUuid() + "?size=128&overlay");
            embed.setDescription("New login - " + username + " (IP: " + ip + ")" + " OS: " + osName);
            embed.setColor(Color.GRAY);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);

            if (validateHwid()) webhook.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getHwid() {
        StringBuilder returnhwid = new StringBuilder();
        String hwid = System.getProperty("user.name") + System.getProperty("user.home") + System.getProperty("os.version") + System.getProperty("os.name");
        for (String s : StringUtil.getSubstrings(hwid)) {
            returnhwid.append(StringUtil.convertToString(s));
        }
        return returnhwid.toString();
    }

    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return (formatter.format(date));
    }
}
