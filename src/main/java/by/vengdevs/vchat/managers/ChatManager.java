package by.vengdevs.vchat.managers;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.classes.Message;
import by.vengdevs.vchat.utils.Color;
import by.vengdevs.vchat.utils.Logger;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChatManager {

    public static final ChatManager instance = new ChatManager();
    private final List<Message> chatHistory = new ArrayList<>();
    private final HashMap<Player, String> transliteratedMessages = new HashMap<>();
    private int newID = 0;

    public void displayMessage(Message message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (message.getAnswerToMessage() == null) player.spigot().sendMessage(MessageManager.instance.addTransliterateButton(player, message));
            else sendAnswerMessage(player, message);
            if (shouldPlayerBeMentioned(player, message)) sendPlayerMentionTitle(player, message.getAuthor());
            if (shouldPlayerBeAnswered(player, message)) sendPlayerAnswerTitle(player, message.getAuthor());
        }

        saveMessageToChatHistory(message);
        Logger.instance.sendConsole(message.getAuthor().getName() + " > " + message.getRawContent());
    }

    public void displayChatHistory(Player player) {
        for (Message message : chatHistory) {
            if (message.getAnswerToMessage() == null) {
                player.spigot().sendMessage(message.getContent());
            } else {
                sendAnswerMessage(player, message);
            }
        }

        player.sendMessage(getConfigString("chat.chat-history.message"));
    }

    public void saveToTransliteratedMessages(Player player, String message) {
        transliteratedMessages.put(player, message);
    }

    public HashMap<Player, String> getTransliteratedMessages() {
        return transliteratedMessages;
    }

    private void saveMessageToChatHistory(Message message) {
        chatHistory.add(message);
    }

    public int getNewID() {
        return this.newID;
    }

    public void setNewID(int newID) {
        this.newID = newID;
    }

    @Nullable
    public Message getMessage(int id) {
        for (int i = chatHistory.size() - 1; i >= 0; i--) {
            if (chatHistory.get(i).getId() == id) {
                return chatHistory.get(i);
            }
        }
        return null;
    }

    private String getConfigString(String path) {
        return Color.convert("&", Objects.requireNonNull(VChat.instance.getConfig().getString(path)));
    }

    private boolean shouldPlayerBeMentioned(Player player, Message message) {
        return message.getMentions().contains(player) && message.getMentioning();
    }

    private boolean shouldPlayerBeAnswered(Player player, Message message) {
        return message.getAnswerToMessage() != null && message.getAnswerToMessage().getAuthor().equals(player);
    }

    private void sendPlayerAnswerTitle(Player player, Player author) {
        String title = getConfigString("chat.answers.title.title").replace("%AUTHOR%", author.getName());
        String subtitle = getConfigString("chat.answers.title.subtitle").replace("%AUTHOR%", author.getName());
        int time = VChat.instance.getConfig().getInt("chat.answers.title.time") * 20;
        player.sendTitle(title, subtitle, 0, time, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
    }

    private void sendPlayerMentionTitle(Player player, Player author) {
        String title = getConfigString("chat.mentions.title.title").replace("%AUTHOR%", author.getName());
        String subtitle = getConfigString("chat.mentions.title.subtitle").replace("%AUTHOR%", author.getName());
        int time = VChat.instance.getConfig().getInt("chat.mentions.title.time") * 20;
        player.sendTitle(title, subtitle, 0, time, 0);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
    }

    private void sendAnswerMessage(Player player, Message message) {
        assert message.getAnswerToMessage() != null;
        String answerPattern = getConfigString("chat.answers.pattern")
                .replace("%NAME%", message.getAnswerToMessage().getAuthor().getName());
        List<BaseComponent> fullComponents = new ArrayList<>();
        fullComponents.add(new TextComponent(answerPattern.split("%MESSAGE%")[0]));
        fullComponents.addAll(Arrays.asList(message.getAnswerToMessage().getContent()));
        player.spigot().sendMessage(fullComponents.toArray(new BaseComponent[0]));
        player.spigot().sendMessage(MessageManager.instance.addTransliterateButton(player, message));
        Logger.instance.sendConsole("┌ ANSWER: " + message.getAnswerToMessage().getAuthor().getName() + " > " + message.getAnswerToMessage().getRawContent());
    }
}
