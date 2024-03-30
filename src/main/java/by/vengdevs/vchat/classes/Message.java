package by.vengdevs.vchat.classes;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Message {

    private final BaseComponent[] content;
    private final Player author;
    private final List<Player> mentions;
    private final String rawContent;
    private final boolean transliterated;
    private final boolean mentioning;
    private final int id;
    @Nullable private final Message answerToMessage;

    public Message(BaseComponent[] content, Player author, List<Player> mentions, String rawContent, boolean transliterated, boolean mentioning, int id, @Nullable Message answerToMessage) {
        this.content = content;
        this.author = author;
        this.mentions = mentions;
        this.rawContent = rawContent;
        this.transliterated = transliterated;
        this.mentioning = mentioning;
        this.id = id;
        this.answerToMessage = answerToMessage;
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
    public boolean getMentioning() {
        return this.mentioning;
    }
    public int getId() {
        return this.id;
    }
    public @Nullable Message getAnswerToMessage() {
        return this.answerToMessage;
    }
}
