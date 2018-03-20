package de.codemakers.bot.supreme.entities;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.util.Standard;
import java.util.Objects;
import java.util.regex.Matcher;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;

/**
 * AdvancedEmote
 *
 * @author Panzer1119
 */
public class AdvancedEmote {

    private final String name;
    private final Emoji emoji;
    private final Emote emote;

    public AdvancedEmote(Emoji emoji) {
        this(emoji.getUnicode(), emoji, null);
    }

    public AdvancedEmote(Emote emote) {
        this(emote.getName(), null, emote);
    }

    public AdvancedEmote(String name, Emoji emoji, Emote emote) {
        this.name = name;
        this.emoji = emoji;
        this.emote = emote;
    }

    public final String getName() {
        return name;
    }

    public final Emoji getEmoji() {
        return emoji;
    }

    public final Emote getEmote() {
        return emote;
    }

    public final boolean isEmoji() {
        return emoji != null;
    }

    public final boolean isCustom() {
        return emote != null;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof AdvancedEmote) {
            final AdvancedEmote emote_ = (AdvancedEmote) object;
            if (isCustom() != emote_.isCustom()) {
                return false;
            }
            if (isCustom()) {
                if (Objects.equals(getEmote(), emote_.getEmote())) {
                    return true;
                } else if (getEmote() != null && emote_.getEmote() != null) {
                    return getEmote().getIdLong() == emote_.getEmote().getIdLong();
                }
            } else {
                return Objects.equals(getEmoji(), emote_.getEmoji());
            }
        } else {
            if (object instanceof MessageReaction) {
                object = ((MessageReaction) object).getReactionEmote();
            }
            if (object instanceof ReactionEmote) {
                final ReactionEmote emote_ = (ReactionEmote) object;
                if (emote_.isEmote() != isCustom()) {
                    return false;
                } else if (emote_.isEmote() && isCustom()) {
                    return emote_.getEmote().getIdLong() == emote.getIdLong();
                } else if (!emote_.isEmote() && isEmoji()) {
                    return emoji.getUnicode().equals(emote_.getName());
                }
            }
        }
        return false;
    }

    @Override
    public final String toString() {
        return String.format("%s: name = %s, isCustom = %b, data = %s", getClass().getSimpleName(), getName(), isCustom(), isCustom() ? getEmote() : getEmoji());
    }

    public static final AdvancedEmote parse(String text) {
        if (ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(text).matches()) {
            return parse(null, text);
        }
        Emoji emoji = EmojiManager.getByUnicode(text);
        if (emoji == null) {
            emoji = EmojiManager.getForAlias(text);
        }
        return new AdvancedEmote(emoji != null ? emoji.getUnicode() : text, emoji, null);
    }

    public static final AdvancedEmote parse(Guild guild, String text) {
        final Matcher matcher = ArgumentList.PATTERN_MARKDOWN_CUSTOM_EMOJI.matcher(text);
        if (guild == null && !matcher.matches()) {
            return parse(text);
        } else if (guild != null) {
            Emote emote = guild.getEmoteById(text);
            if (emote == null) {
                emote = guild.getEmotesByName(text, false).stream().findFirst().orElse(null);
            }
            return new AdvancedEmote(emote != null ? emote.getName() : text, null, emote);
        } else {
            final String id = matcher.group(2);
            return new AdvancedEmote(matcher.group(1), null, Standard.getGuilds().stream().filter((advancedGuild) -> advancedGuild.getGuild() != null).map((advancedGuild) -> advancedGuild.getGuild().getEmoteById(id)).filter((emote) -> emote != null).findFirst().orElse(null));
        }
    }

    public static final AdvancedEmote ofReactionEmote(ReactionEmote emote) {
        if (emote.isEmote()) {
            return new AdvancedEmote(emote.getEmote());
        } else {
            return parse(emote.getName());
        }
    }

}
