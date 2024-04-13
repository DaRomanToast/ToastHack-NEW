package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.florianmichael.dietrichevents2.DietrichEvents2;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscordNotifierFeature extends Feature implements UpdateListener {

    public DiscordNotifierFeature() {
        super("DiscordNotifier", "Misc");
    }

    public void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate() {
        toggle();
        String DiscordWebHook = "https://discord.com/api/webhooks/1146364217721622600/vb_A706AuGVFKT27pNGC7Fd_xOF9q9npOmcOoKUWg8IpOBVtXqrEtxyOA2vM5-GrJxJf";
        String title = "Backup.";
        String username = Client.MC.getSession().getUsername();

        assert Client.MC.player != null;
        String coordinates = Client.MC.player.getBlockPos().getX() + " " + Client.MC.player.getBlockPos().getY() + " " + Client.MC.player.getBlockPos().getZ();
        String server = Client.MC.isInSingleplayer() ? "SinglePlayer" : Client.MC.getCurrentServerEntry().address;

        String message = "<@&980520336560914432> " + "User: " + username + " Needs Backup. " + "Position: " + coordinates + " Server Address: " + server;

        JsonObject embed = new JsonObject();
        embed.addProperty("title", title);
        embed.addProperty("description", message);
        embed.addProperty("color", 15258703);
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        JsonObject postContent = new JsonObject();
        postContent.add("embeds", embeds);
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        client.sendAsync(HttpRequest.newBuilder(URI.create(DiscordWebHook)).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(postContent))).header("Content-Type", "application/json").build(), HttpResponse.BodyHandlers.discarding());
    }
}
