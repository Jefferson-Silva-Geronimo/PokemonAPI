package model;

/**
 * Classe que representa um movimento aprendido por um Pokémon em um determinado nível.
 *
 * Relaciona um movimento com o nível necessário para aprendê-lo.
 *
 * Objetivo:
 * - Controlar progressão de habilidades do Pokémon
 * - Permitir aprendizagem automática ao subir de nível
 *
 * Conceitos aplicados:
 * - Associação entre objetos
 * - Regra de progressão
 */
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