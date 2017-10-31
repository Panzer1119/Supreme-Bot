package de.codemakers.bot.supreme.game;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.entities.AdvancedEmote;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MultiObject;
import de.codemakers.bot.supreme.entities.MultiObjectHolder;
import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.permission.ReactionPermissionFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.awt.Color;
import java.util.HashMap;
import javax.swing.Timer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;

/**
 * GameOfLife
 *
 * @author Panzer1119
 */
public class GameOfLife extends Game {

    private static final String EMOJI_ALIVE = Emoji.SQUARE_LARGE_BLACK;
    private static final String EMOJI_DEAD = Emoji.SQUARE_LARGE_WHITE;

    private final HashMap<String, HashMap<String, Object>> generations = new HashMap<>();
    private int generation = 0;
    private Timer timer = null;
    private EmbedBuilder builder_header = null;
    private Field field = null;
    private Message message_header = null;
    private Message message_field = null;
    private boolean running = false;
    private boolean show_coordinates = false;
    private int max_x = -1;
    private int max_y = -1;
    private int max_z = -1;
    private int max_w = -1;
    private GameOfLifeRule rule = GameOfLife.STANDARD_GAME_OF_LIFE_2D_RULE;
    private int generations_per_turn = 1;
    private int turn_delay = 4000;

    @Override
    public final boolean startGame(ArgumentList arguments, MessageEvent event) {
        builder_header = new EmbedBuilder();
        builder_header.setColor(Color.GREEN);
        builder_header.addField("Status", "Not running", false);
        field = new MessageEmbed.Field("Description", String.format("Alive Cell: %s%nDead Cell: %s", EMOJI_ALIVE, EMOJI_DEAD), false);
        builder_header.addField(field);
        updateMessageHeader();
        message_header = event.sendAndWaitMessage(builder_header.build());
        message_field = event.sendAndWaitMessage("Field");
        ReactionListener.registerListener(message_field, AdvancedEmote.parse(Emoji.PLAY), (reaction, emote, guild, user) -> setRunning(true), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse(Emoji.REWIND), (reaction, emote, guild, user) -> goGeneration(-1), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse(Emoji.FAST_FORWARD), (reaction, emote, guild, user) -> goGeneration(1), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse(Emoji.STOP), (reaction, emote, guild, user) -> setRunning(false), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse("arrow_up_small"), (reaction, emote, guild, user) -> setTurnDelay(getTurnDelay() + 250), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse("arrow_down_small"), (reaction, emote, guild, user) -> setTurnDelay(getTurnDelay() - 250), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse("arrow_up"), (reaction, emote, guild, user) -> setGenerationsPerTurn(getGenerationsPerTurn() + 1), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse("arrow_down"), (reaction, emote, guild, user) -> setGenerationsPerTurn(getGenerationsPerTurn() - 1), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse("symbols"), (reaction, emote, guild, user) -> setShowingCoordinates(!isShowingCoordinates()), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        ReactionListener.registerListener(message_field, AdvancedEmote.parse(Emoji.NO), (reaction, emote, guild, user) -> endGame(null, event), null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
        max_x = 10;
        max_y = 10;
        if (arguments.isSize(1, -1)) {
            try {
                max_x = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        if (arguments.isSize(1, -1)) {
            try {
                max_y = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        if (arguments.isSize(1, -1)) {
            try {
                max_z = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        if (arguments.isSize(1, -1)) {
            try {
                max_w = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        resetField();
        return true;
    }

    @Override
    public final boolean endGame(ArgumentList arguments, MessageEvent event) {
        setRunning(false);
        generations.keySet().stream().forEach((g) -> {
            generations.get(g).clear();
        });
        generations.clear();
        final MultiObjectHolder holder = MultiObjectHolder.of(event.getGuild(), event.getAuthor(), event.getTextChannel());
        final MultiObject<GameOfLife> multiObject = MultiObject.getFirstMultiObject(GameOfLife.class.getName(), holder);
        multiObject.unregister();
        Util.deleteMessage(message_field, 0);
        Util.deleteMessage(message_header, 0);
        builder_header = null;
        message_header = null;
        message_field = null;
        return true;
    }

    @Override
    public final boolean sendInput(ArgumentList arguments, MessageEvent event) {
        if (arguments == null || event == null) {
            return false;
        }
        int x = -2;
        int y = -2;
        int z = -2;
        int w = -2;
        if (arguments.isSize(1, -1)) {
            try {
                x = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        if (arguments.isSize(1, -1)) {
            try {
                y = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        if (arguments.isSize(1, -1)) {
            try {
                z = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        if (arguments.isSize(1, -1)) {
            try {
                w = Integer.parseInt(arguments.consumeFirst());
            } catch (NumberFormatException ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        }
        if (x >= max_x) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s your x value is out of bound (%d >= %d)!", Emoji.WARNING, event.getAuthor().getAsMention(), x, max_x);
            return false;
        }
        if (y >= max_y) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s your y value is out of bound (%d >= %d)!", Emoji.WARNING, event.getAuthor().getAsMention(), y, max_y);
            return false;
        }
        if (z >= max_z) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s your z value is out of bound (%d >= %d)!", Emoji.WARNING, event.getAuthor().getAsMention(), z, max_z);
            return false;
        }
        if (w >= max_w) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s your w value is out of bound (%d >= %d)!", Emoji.WARNING, event.getAuthor().getAsMention(), w, max_w);
            return false;
        }
        Object temp = null;
        if (w >= 0) { //4D
            temp = getCell(generation, x, y, z, w);
            if (temp == null) {
                temp = EMOJI_ALIVE;
            } else if (temp == EMOJI_ALIVE) {
                temp = EMOJI_DEAD;
            } else {
                temp = EMOJI_ALIVE;
            }
            putCell(temp, generation, x, y, z, w);
            updateMessages();
        } else if (z >= 0) { //3D
            temp = getCell(generation, x, y, z);
            if (temp == null) {
                temp = EMOJI_ALIVE;
            } else if (temp == EMOJI_ALIVE) {
                temp = EMOJI_DEAD;
            } else {
                temp = EMOJI_ALIVE;
            }
            putCell(temp, generation, x, y, z);
            updateMessages();
        } else if (y >= 0) { //2D
            temp = getCell(generation, x, y);
            if (temp == null) {
                temp = EMOJI_ALIVE;
            } else if (temp == EMOJI_ALIVE) {
                temp = EMOJI_DEAD;
            } else {
                temp = EMOJI_ALIVE;
            }
            putCell(temp, generation, x, y);
            updateMessages();
        } else if (x >= 0) { //1D
            temp = getCell(generation, x);
            if (temp == null) {
                temp = EMOJI_ALIVE;
            } else if (temp == EMOJI_ALIVE) {
                temp = EMOJI_DEAD;
            } else {
                temp = EMOJI_ALIVE;
            }
            putCell(temp, generation, x);
            updateMessages();
        } else {
            return false;
        }
        return true;
    }

    public final double getSpeed() {
        return (1_000.0 * generations_per_turn) / (turn_delay * 1.0);
    }

    public final int getGenerationsPerTurn() {
        return generations_per_turn;
    }

    public final GameOfLife setGenerationsPerTurn(int generations_per_turn) {
        if (this.generations_per_turn != generations_per_turn) {
            this.generations_per_turn = generations_per_turn;
            if (isRunning()) {
                setRunning(false);
                timer = null;
                setRunning(true);
            } else {
                timer = null;
            }
            updateMessageHeader();
        }
        this.generations_per_turn = generations_per_turn;
        return this;
    }

    public final int getTurnDelay() {
        return turn_delay;
    }

    public final GameOfLife setTurnDelay(int turn_delay) {
        if (turn_delay <= 0) {
            return this;
        }
        if (this.turn_delay != turn_delay) {
            this.turn_delay = turn_delay;
            if (isRunning()) {
                setRunning(false);
                timer = null;
                setRunning(true);
            } else {
                timer = null;
            }
            updateMessageHeader();
        }
        this.turn_delay = turn_delay;
        return this;
    }

    private final boolean resetField() {
        generation = 0;
        if (is1D()) {
            for (int x = 0; x < max_x; x++) {
                putCell(EMOJI_DEAD, generation, x);
            }
        } else if (is2D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    putCell(EMOJI_DEAD, generation, x, y);
                }
            }
        } else if (is3D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    for (int z = 0; z < max_z; z++) {
                        putCell(EMOJI_DEAD, generation, x, y, z);
                    }
                }
            }
        } else if (is4D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    for (int z = 0; z < max_z; z++) {
                        for (int w = 0; w < max_w; w++) {
                            putCell(EMOJI_DEAD, generation, x, y, z, w);
                        }
                    }
                }
            }
        } else {
            return false;
        }
        updateMessages();
        return true;
    }

    private final GameOfLife updateMessages() {
        updateMessageHeader();
        updateMessageField();
        return this;
    }

    private final GameOfLife updateMessageHeader() {
        if (builder_header == null || message_field == null) {
            return this;
        }
        builder_header.setTitle(String.format("GameOfLife Generation: %d", generation));
        builder_header.clearFields();
        final double speed = getSpeed();
        builder_header.addField("Status", String.format("%sRunning, Speed: %d Generation%s per %.2f Second%s (%.2f Generation%s per Second)", (isRunning() ? "" : "Not "), generations_per_turn, generations_per_turn != 1 ? "s" : "", turn_delay / 1_000.0, turn_delay != 1_000 ? "s" : "", speed, speed != 1 ? "s" : ""), false);
        builder_header.addField(field);
        message_header.editMessage(builder_header.build()).queue();
        return this;
    }

    private final GameOfLife updateMessageField() {
        if (message_field == null) {
            return this;
        }
        message_field.editMessage(drawField(show_coordinates)).queue();
        return this;
    }

    private final String drawField(boolean withCoords) {
        String out = "";
        if (is1D()) {
            for (int x = 0; x < max_x; x++) {
                out += getCell(generation, x) + (withCoords ? String.format("X:%d", x) : "");
            }
        } else if (is2D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    out += getCell(generation, x, y) + (withCoords ? String.format("X:%d,Y:%d", x, y) : "");
                }
                out += Standard.NEW_LINE_DISCORD;
            }
        } else if (is3D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    for (int z = 0; z < max_z; z++) {
                        out += getCell(generation, x, y, z) + (withCoords ? String.format("X:%d,Y:%d,Z:%d", x, y, z) : "");
                    }
                    out += Standard.NEW_LINE_DISCORD;
                }
                out += Standard.NEW_LINE_DISCORD;
            }
        } else if (is4D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    for (int z = 0; z < max_z; z++) {
                        for (int w = 0; w < max_w; w++) {
                            out += getCell(generation, x, y, z, w) + (withCoords ? String.format("X:%d,Y:%d,Z:%d,W:%d", x, y, z, w) : "");
                        }
                        out += Standard.NEW_LINE_DISCORD;
                    }
                    out += Standard.NEW_LINE_DISCORD;
                }
                out += Standard.NEW_LINE_DISCORD;
            }
        } else {
            return "";
        }
        return out;
    }

    public final boolean isRunning() {
        return running;
    }

    public final GameOfLife setRunning(boolean running) {
        if (this.running == running) {
            return this;
        }
        this.running = running;
        if (this.running) {
            if (timer == null) {
                timer = new Timer(turn_delay, (e) -> goGeneration(generations_per_turn));
            }
            timer.start();
        } else if (timer != null) {
            timer.stop();
        }
        updateMessages();
        return this;
    }

    public final boolean isShowingCoordinates() {
        return show_coordinates;
    }

    public final GameOfLife setShowingCoordinates(boolean show_coordinates) {
        if (this.show_coordinates != show_coordinates) {
            this.show_coordinates = show_coordinates;
            updateMessageField();
        }
        this.show_coordinates = show_coordinates;
        return this;
    }

    public final boolean goGeneration(int times) {
        if (times < 0 && (generation + times) < 0) {
            return false;
        } else if (times == 0) {
            return true;
        }
        if (times < 0) {
            generation += times;
        } else {
            for (int i = 0; i < times; i++) {
                oneGeneration();
            }
        }
        updateMessages();
        return true;
    }

    private final boolean oneGeneration() {
        generation++;
        int temp = generation;
        while (generations.containsKey("" + temp)) {
            generations.remove("" + temp);
            temp++;
        }
        if (is1D()) {
            for (int x = 0; x < max_x; x++) {
                putCell(rule.process1DCell(getCell(generation - 1, x), this, generation, x), generation, x);
            }
        } else if (is2D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    putCell(rule.process2DCell(getCell(generation - 1, x, y), this, generation, x, y), generation, x, y);
                }
            }
        } else if (is3D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    for (int z = 0; z < max_z; z++) {
                        putCell(rule.process3DCell(getCell(generation - 1, x, y, z), this, generation, x, y, z), generation, x, y, z);
                    }
                }
            }
        } else if (is4D()) {
            for (int x = 0; x < max_x; x++) {
                for (int y = 0; y < max_y; y++) {
                    for (int z = 0; z < max_z; z++) {
                        for (int w = 0; w < max_w; w++) {
                            putCell(rule.process4DCell(getCell(generation - 1, x, y, z, w), this, generation, x, y, z, w), generation, x, y, z, w);
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public final boolean is1D() {
        return max_x != -1 && max_y == -1 && max_z == -1 && max_w == -1;
    }

    public final boolean is2D() {
        return max_x != -1 && max_y != -1 && max_z == -1 && max_w == -1;
    }

    public final boolean is3D() {
        return max_x != -1 && max_y != -1 && max_z != -1 && max_w == -1;
    }

    public final boolean is4D() {
        return max_x != -1 && max_y != -1 && max_z != -1 && max_w != -1;
    }

    public final Object getCell(int generation, int x) {
        final HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            return null;
        }
        return field.get(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x));
    }

    public final Object getCell(int generation, int x, int y) {
        final HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            return null;
        }
        return field.get(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x, y));
    }

    public final Object getCell(int generation, int x, int y, int z) {
        final HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            return null;
        }
        return field.get(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x, y, z));
    }

    public final Object getCell(int generation, int x, int y, int z, int w) {
        final HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            return null;
        }
        return field.get(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x, y, z, w));
    }

    public final Object putCell(Object object, int generation, int x) {
        HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            field = new HashMap<>();
            generations.put("" + generation, field);
        }
        return field.put(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x), object);
    }

    public final Object putCell(Object object, int generation, int x, int y) {
        HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            field = new HashMap<>();
            generations.put("" + generation, field);
        }
        return field.put(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x, y), object);
    }

    public final Object putCell(Object object, int generation, int x, int y, int z) {
        HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            field = new HashMap<>();
            generations.put("" + generation, field);
        }
        return field.put(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x, y, z), object);
    }

    public final Object putCell(Object object, int generation, int x, int y, int z, int w) {
        HashMap<String, Object> field = generations.get("" + generation);
        if (field == null) {
            field = new HashMap<>();
            generations.put("" + generation, field);
        }
        return field.put(Util.joinNumbers(Standard.STANDARD_NUMBER_SEPARATOR, x, y, z, w), object);
    }

    private static final GameOfLifeRule STANDARD_GAME_OF_LIFE_2D_RULE = new GameOfLifeRule() {
        @Override
        public Object process2DCell(Object object, GameOfLife game, int generation_new, int x, int y) {
            int living_neighbours = 0;
            for (int x_ = -1; x_ < 2; x_++) {
                for (int y_ = -1; y_ < 2; y_++) {
                    if (x_ != 0 || y_ != 0) {
                        if (game.getCell(generation_new - 1, x_ + x, y_ + y) == EMOJI_ALIVE) {
                            living_neighbours++;
                        }
                    }
                }
            }
            boolean live = false;
            if (living_neighbours == 3 && (object == EMOJI_DEAD)) {
                live = true;
            } else if (living_neighbours < 2) {
                live = false;
            } else if ((living_neighbours == 2 || living_neighbours == 3) && (object == EMOJI_ALIVE)) {
                live = true;
            } else if (living_neighbours > 3) {
                live = false;
            }
            return (live ? EMOJI_ALIVE : EMOJI_DEAD);
        }
    };

}
