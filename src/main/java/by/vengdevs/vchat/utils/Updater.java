package by.vengdevs.vchat.utils;

import by.vengdevs.vchat.VChat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    public static void getUpdates() {
        try {
            String latestVersion = getLatestVersion();
            if (latestVersion != null && !latestVersion.equals(VChat.instance.getPluginMeta().getVersion())) {
                for (int i = 0; i < 100; i++) Logger.instance.sendConsole(ChatColor.translateAlternateColorCodes('&', "&5[vChat]&r &cNEW VERSION FOUND! THE SERVER WILL AUTOMATICALLY RESTART AFTER THE UPDATE!"));
                updatePlugin();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
            } else {
                Logger.instance.sendConsole("&5[vChat]&r &aYou have the latest version installed!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updatePlugin() {
        String downloadUrl = "https://github.com/VengDevs/vChat/releases/latest/download/vChat.jar";

        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream("plugins/vChat.jar");

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getLatestVersion() throws IOException {
        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
        String url = "https://api.github.com/repos/VengDevs/vChat/releases/latest";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            return jsonObject.get("tag_name").getAsString();
        } else {
            return null;
        }
    }
}
