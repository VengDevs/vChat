package by.vengdevs.vchat.utils;

import by.vengdevs.vchat.VChat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Emoji {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, String> emojiMap;

    public static void loadEmojis() {
        File file = new File(VChat.instance.getDataFolder(), "emojis.json");
        if (!file.exists()) {
            emojiMap = new HashMap<>();
            emojiMap.put("heart", "♥");
            emojiMap.put("thumbsup", "\uD83D\uDC4D");
            emojiMap.put("tu", "\uD83D\uDC4D");
            emojiMap.put("thumbsdown", "\uD83D\uDC4E");
            emojiMap.put("td", "\uD83D\uDC4E");
            emojiMap.put("happy", "☺");
            emojiMap.put("skull", "\uD83D\uDC80");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(emojiMap, writer);
            } catch (IOException ignored) {
            }
        }

        try (FileReader reader = new FileReader(file)) {
            emojiMap = gson.fromJson(reader, HashMap.class);
        } catch (IOException ignored) {
        }
    }

    public static String getEmoji(String emojiName) {
        String emoji = emojiMap.getOrDefault(emojiName, null);
        if (emoji != null) return ChatColor.translateAlternateColorCodes('&', emoji);
        return null;
    }

    public static Map<String, String> getAllEmojis() {
        return emojiMap;
    }
}
