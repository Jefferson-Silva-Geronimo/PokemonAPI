import game.Game;
import repository.DatabaseInitializer;

/**
 * Classe principal responsável por iniciar a aplicação.
 *
 * Inicializa o banco de dados e inicia o fluxo do jogo.
 *
 * Objetivo:
 * - Configurar ambiente inicial
 * - Iniciar execução do sistema
 *
 * Conceitos aplicados:
 * - Ponto de entrada da aplicação
 */
public class Main {

    public static void main(String[] args) {
        try {
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.criarTabelas();

            Game game = new Game();
            game.start();

        } catch (Exception e) {
            System.out.println("Ocorreu um erro inesperado ao iniciar o jogo.");
            System.out.println("Detalhes: " + e.getMessage());
            System.out.println("O programa será encerrado com segurança.");
        }
    }
}