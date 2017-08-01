package de.codemakers.bot.supreme.game;

/**
 * GameOfLifeRule
 *
 * @author Panzer1119
 */
public abstract class GameOfLifeRule {

    public Object process1DCell(Object object, GameOfLife game, int generation_new, int x) {
        return object;
    }

    public Object process2DCell(Object object, GameOfLife game, int generation_new, int x, int y) {
        return object;
    }

    public Object process3DCell(Object object, GameOfLife game, int generation_new, int x, int y, int z) {
        return object;
    }

    public Object process4DCell(Object object, GameOfLife game, int generation_new, int x, int y, int z, int w) {
        return object;
    }

}
