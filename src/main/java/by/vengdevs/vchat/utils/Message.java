package by.vengdevs.vchat.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

import java.util.List;

public class Message {

    private final BaseComponent[] content;
    private final Player author;
    private final List<Player> mentions;
    private final String rawContent;
    private final boolean transliterated;

    public Message(BaseComponent[] content, Player author, List<Player> mentions, String rawContent, boolean transliterated) {
        this.content = content;
        this.author = author;
        this.mentions = mentions;
        this.rawContent = rawContent;
        this.transliterated = transliterated;
    }

    public BaseComponent[] getContent() {
        return this.content;
    }

    public Player getAuthor() {
        return this.author;
    }

    public List<Player> getMentions() {
        return this.mentions;
    }

    public String getRawContent() {
        return this.rawContent;
    }

    public boolean getTransliterated() {
        return this.transliterated;
    }
}
