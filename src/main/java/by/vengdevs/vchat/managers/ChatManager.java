package by.vengdevs.vchat.managers;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.classes.Message;
import by.vengdevs.vchat.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatManager {

    public static final ChatManager instance = new ChatManager();

    private static final List<Message> chatHistory = new ArrayList<>();

    public void displayMessage(Message message) {
        String title = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.mentions.title.title")));
        String subtitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.mentions.title.subtitle")));
        int time = VChat.instance.getConfig().getInt("chat.mentions.title.time") * 20;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(MessageManager.instance.addTransliterateButton(player, message));
            if (message.getMentions().contains(player) && message.getMentioning()) {
                player.sendTitle(title.replace("%AUTHOR%", message.getAuthor().getName()), subtitle.replace("%AUTHOR%", message.getAuthor().getName()), 0, time, 0);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
            }
        }

        saveMessageToChatHistory(message);
        Logger.instance.sendConsole(message.getAuthor().getName() + " > " + message.getRawContent());
    }

    private void saveMessageToChatHistory(Message message) {
        chatHistory.add(message);
    }

    public void displayChatHistory(Player player) {
        for (Message message : chatHistory) {
            player.spigot().sendMessage(message.getContent());
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(VChat.instance.getConfig().getString("chat.chat-history.message"))));
    }
}
