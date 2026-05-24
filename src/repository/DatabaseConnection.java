package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por fornecer a conexão com o banco de dados.
 *
 * Utiliza o banco H2 local para persistência dos dados do jogo.
 *
 * Objetivo:
 * - Centralizar criação de conexões
 * - Facilitar manutenção da camada de persistência
 *
 * Conceitos aplicados:
 * - Singleton implícito (classe utilitária)
 * - JDBC
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:h2:./pokemon_db";

    private DatabaseConnection() {
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}