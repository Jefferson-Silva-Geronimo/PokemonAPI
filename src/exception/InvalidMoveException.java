package exception;

/**
 * Exceção lançada quando um movimento não pode ser executado.
 *
 * Pode ocorrer em situações como falta de PP, dados inválidos
 * ou tentativa de uso incorreto de um movimento.
 *
 * Objetivo:
 * - Impedir execução inválida de golpes
 * - Garantir consistência da batalha
 *
 * Conceitos aplicados:
 * - Validação de regras
 * - Exceções customizadas
 */
public class InvalidMoveException extends Exception {

    public InvalidMoveException(String message) {
        super(message);
    }
}