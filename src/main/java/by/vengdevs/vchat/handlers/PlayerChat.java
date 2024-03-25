package by.vengdevs.vchat.handlers;

import by.vengdevs.vchat.VChat;
import by.vengdevs.vchat.utils.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;


public class PlayerChat implements Listener {

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (!VChat.instance.getConfig().getBoolean("chat.enabled")) return;
        event.setCancelled(true);

        MessageManager.instance.formatMessage(event.getMessage(), event.getPlayer(), false);
    }
}
