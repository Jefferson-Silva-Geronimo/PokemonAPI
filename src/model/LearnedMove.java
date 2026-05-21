package model;

public class LearnedMove {

    private Move move;
    private int levelLearnedAt;

    public LearnedMove(Move move, int levelLearnedAt) {
        setMove(move);
        setLevelLearnedAt(levelLearnedAt);
    }

    public Move getMove() {
        return move;
    }

    public int getLevelLearnedAt() {
        return levelLearnedAt;
    }

    public void setMove(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("Movimento aprendido não pode ser nulo.");
        }

        this.move = move;
    }

    public void setLevelLearnedAt(int levelLearnedAt) {
        this.levelLearnedAt = Math.max(1, levelLearnedAt);
    }
}