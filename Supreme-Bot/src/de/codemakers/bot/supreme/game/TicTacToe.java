package de.codemakers.bot.supreme.game;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import java.util.List;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

/**
 * TicTacToe
 *
 * @author Panzer1119 &amp; Alien Ideology <alien.ideology at alien.org>
 */
public class TicTacToe extends Game {

    private final Board game = new Board(3, 3);
    private MessageEvent event_started;
    private User starter;
    private User opponent;
    private String piece;
    private int row;
    private int column;
    private User turn;
    private Message message_header = null;
    private Message message_board = null;

    @Override
    public final boolean startGame(ArgumentList arguments, MessageEvent event) {
        try {
            event_started = event;
            setGuildId(event.getGuild().getId());
            starter = event.getAuthor();
            final List<User> mentionedUsers = event.getMessage().getMentionedUsers();
            try {
                opponent = mentionedUsers.get(0);
            } catch (Exception ex) {
                event.sendMessage(Emoji.WARNING + " Please mention a person to start the game.");
            }
            turn = starter;
            message_header = event.sendAndWaitMessage(Standard.getMessageEmbed(Color.GREEN, null).addField(String.format("%s TicTacToe", Emoji.GAME), String.format("Starter: %s%nOpponent%s", starter.getAsMention(), opponent.getAsMention()), true).build());
            message_board = event.sendAndWaitMessage(game.toString());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public final boolean endGame(ArgumentList arguments, MessageEvent event) {
        try {
            if (event.getAuthor() == starter || event.getAuthor() == opponent) {
                event.sendMessage(Standard.getMessageEmbed(Color.GREEN, null).setTitle(String.format("%s TicTacToe", Emoji.GAME), null).setFooter(String.format("%s ended the game.", event.getAuthor().getName()), null).build());
                game.clearBoard();
                Standard.getAdvancedGuild(getGuildId()).putData("game", null);
                return true;
            } else {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s do not interfere the game!", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public final boolean sendInput(ArgumentList arguments, MessageEvent event) {
        if (arguments == null || event == null) {
            return false;
        }
        try {
            switch (arguments.consumeFirst()) {
                case "1":
                    row = 0;
                    column = 0;
                    break;
                case "2":
                    row = 0;
                    column = 1;
                    break;
                case "3":
                    row = 0;
                    column = 2;
                    break;
                case "4":
                    row = 1;
                    column = 0;
                    break;
                case "5":
                    row = 1;
                    column = 1;
                    break;
                case "6":
                    row = 1;
                    column = 2;
                    break;
                case "7":
                    row = 2;
                    column = 0;
                    break;
                case "8":
                    row = 2;
                    column = 1;
                    break;
                case "9":
                    row = 2;
                    column = 2;
                    break;
                default:
                    throw new StringIndexOutOfBoundsException();
            }
            if (event.getAuthor() == opponent) {
                piece = "O";
            } else if (event.getAuthor() == starter) {
                piece = "X";
            }
            if (event.getAuthor() == starter || event.getAuthor() == opponent) {
                if (event.getAuthor() != turn) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s it's not your turn yet!", Emoji.WARNING, event.getAuthor().getAsMention());
                    return false;
                }
            } else {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s do not interfere the game!", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
            if (!game.isOccupied(row, column)) {
                game.addPiece(new Piece(piece), row, column);
                game.drawBoard();
            } else {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the place is occupied. Use your eyes!", Emoji.WARNING, event.getAuthor().getAsMention());
                return false;
            }
            if (game.getWinner().equals("X")) {
                event.sendMessageFormat("%s Player %s wins!", Emoji.NO, starter.getAsMention());
                game.clearBoard();
                Standard.getAdvancedGuild(getGuildId()).putData("game", null);
            } else if (game.getWinner().equals("O")) {
                event.sendMessageFormat("%s Player %s wins!", Emoji.YES, opponent.getAsMention());
                game.clearBoard();
                Standard.getAdvancedGuild(getGuildId()).putData("game", null);
            } else if (game.isDraw()) {
                event.sendMessageFormat("%s Draw, no winner. %s", Emoji.NO, Emoji.YES);
                game.clearBoard();
                Standard.getAdvancedGuild(getGuildId()).putData("game", null);
            } else {
                switchTurn();
            }
            return true;
        } catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
            return false;
        } catch (ArrayIndexOutOfBoundsException ex) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s invalid place!", Emoji.WARNING, event.getAuthor().getAsMention());
            return false;
        }
    }

    private final void switchTurn() {
        if (starter == turn) {
            turn = opponent;
        } else {
            turn = starter;
        }
    }

    private class Board {

        private final int rows;
        private final int cols;
        private final Piece[][] board;
        private int round;

        public Board(int r, int c) {
            rows = r;
            cols = c;
            board = new Piece[r][c];
            round = 0;
            Piece p;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    p = new Piece();
                    addPiece(p, i, j);
                }
            }
        }

        public final Board drawBoard() {
            message_header.editMessage(Standard.getMessageEmbed(Color.GREEN, null).setTitle(String.format("%s Current Board (Round %d)%n", Emoji.GAME, round), null).setFooter(String.format("%s finished his/her turn", turn.getName()), null).build()).queue();
            message_board.editMessage(toString()).queue();
            round++;
            return this;
        }

        public final Board addPiece(Piece x, int r, int c) {
            board[r][c] = x;
            return this;
        }

        public final Piece[][] getBoard() {
            return board;
        }

        public final boolean isOccupied(int r, int c) {
            final Piece p = board[r][c];
            final String q = p.getID();
            return !q.equals("  ");
        }

        public final Board clearBoard() {
            Piece p;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    p = new Piece();
                    addPiece(p, i, j);
                }
            }
            return this;
        }

        public final boolean isDraw() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (!isOccupied(i, j)) {
                        return false;
                    }
                }
            }
            return true;
        }

        public final String getWinner() {
            if (board[0][0].equals(board[0][1]) && board[0][1].equals(board[0][2])) {
                return board[0][0].getID();
            } else if (board[1][0].equals(board[1][1]) && board[1][1].equals(board[1][2])) {
                return board[1][0].getID();
            } else if (board[2][0].equals(board[2][1]) && board[2][1].equals(board[2][2])) {
                return board[2][0].getID();
            } else if (board[0][0].equals(board[1][0]) && board[1][0].equals(board[2][0])) {
                return board[0][0].getID();
            } else if (board[0][1].equals(board[1][1]) && board[1][1].equals(board[2][1])) {
                return board[0][1].getID();
            } else if (board[0][2].equals(board[1][2]) && board[1][2].equals(board[2][2])) {
                return board[0][2].getID();
            } else if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
                return board[0][0].getID();
            } else if (board[2][0].equals(board[1][1]) && board[1][1].equals(board[0][2])) {
                return board[2][0].getID();
            } else {
                return "none";
            }
        }

        private final String getEmojiPos(int r, int c) {
            String emoji = "";
            switch (r) {
                case 0:
                    switch (c) {
                        case 0:
                            emoji = Emoji.ONE;
                            break;
                        case 1:
                            emoji = Emoji.TWO;
                            break;
                        case 2:
                            emoji = Emoji.THREE;
                            break;
                        default:
                            break;
                    }
                    break;
                case 1:
                    switch (c) {
                        case 0:
                            emoji = Emoji.FOUR;
                            break;
                        case 1:
                            emoji = Emoji.FIVE;
                            break;
                        case 2:
                            emoji = Emoji.SIX;
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    switch (c) {
                        case 0:
                            emoji = Emoji.SEVEN;
                            break;
                        case 1:
                            emoji = Emoji.EIGHT;
                            break;
                        case 2:
                            emoji = Emoji.NINE;
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
            if (isOccupied(r, c)) {
                if (getBoard()[r][c].getID().equals("X")) {
                    emoji = Emoji.NO;
                }
                if (getBoard()[r][c].getID().equals("O")) {
                    emoji = Emoji.YES;
                }
            }
            return emoji;
        }

        @Override
        public final String toString() {
            String out = "";
            for (int i = 0; i < rows; i++) {
                for (int z = 0; z < cols; z++) {
                    out += getEmojiPos(i, z);
                }
                out += Standard.NEW_LINE_DISCORD;
            }
            return out;
        }

    }

    public class Piece {

        private final String id;

        Piece() {
            id = "  ";
        }

        Piece(String x) {
            id = x;
        }

        public final String getID() {
            return id;
        }

        public final boolean equals(Piece p) {
            return this.getID().equals(p.getID()) && !this.getID().equals(" ");
        }

    }
}
