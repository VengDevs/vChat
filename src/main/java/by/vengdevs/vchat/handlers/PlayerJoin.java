package by.vengdevs.vchat.handlers;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.managers.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (VChat.instance.getConfig().getBoolean("chat.chat-history.enabled")) {
            ChatManager.instance.displayChatHistory(event.getPlayer());
        }
    }
}
