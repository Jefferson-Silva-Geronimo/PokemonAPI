package repository;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Classe responsável pela criação das tabelas no banco de dados.
 *
 * Executada no início do sistema para garantir que a estrutura
 * necessária existe.
 *
 * Objetivo:
 * - Criar tabelas no banco H2
 * - Preparar ambiente de persistência
 *
 * Conceitos aplicados:
 * - Banco de dados
 */

public class DatabaseInitializer {

    public void criarTabelas() {
        try (Connection conn = DatabaseConnection.get();
             Statement st = conn.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS pokemon (
                    id INT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    hp INT NOT NULL,
                    attack INT NOT NULL,
                    basico BOOLEAN NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS pokemon_ability (
                    id IDENTITY PRIMARY KEY,
                    pokemon_id INT NOT NULL,
                    nome VARCHAR(100) NOT NULL,
                    escondida BOOLEAN NOT NULL,
                    FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS pokemon_move (
                    id IDENTITY PRIMARY KEY,
                    pokemon_id INT NOT NULL,
                    nome VARCHAR(100) NOT NULL,
                    pp_max INT NOT NULL,
                    tipo VARCHAR(50) NOT NULL,
                    precisao INT NOT NULL,
                    poder INT NOT NULL,
                    level_learned_at INT NOT NULL,
                    FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS pokemon_evolution (
                    pokemon_id INT PRIMARY KEY,
                    evolution_name VARCHAR(100) NOT NULL,
                    min_level INT NOT NULL,
                    hp INT NOT NULL,
                    attack INT NOT NULL,
                    FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS save_game (
                    id IDENTITY PRIMARY KEY,
                    nome_jogador VARCHAR(100) NOT NULL,
                    pokemon_id INT NOT NULL,
                    nome_pokemon VARCHAR(100) NOT NULL,
                    hp INT NOT NULL,
                    max_hp INT NOT NULL,
                    attack INT NOT NULL,
                    level INT NOT NULL,
                    xp INT NOT NULL,
                    potions INT NOT NULL,
                    evoluiu BOOLEAN NOT NULL
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS save_move (
                    id IDENTITY PRIMARY KEY,
                    save_id INT NOT NULL,
                    nome VARCHAR(100) NOT NULL,
                    pp_atual INT NOT NULL,
                    pp_max INT NOT NULL,
                    tipo VARCHAR(50) NOT NULL,
                    precisao INT NOT NULL,
                    poder INT NOT NULL,
                    FOREIGN KEY (save_id) REFERENCES save_game(id) ON DELETE CASCADE
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS pokemon_type (
                    id IDENTITY PRIMARY KEY,
                    pokemon_id INT NOT NULL,
                    tipo VARCHAR(50) NOT NULL,
                    FOREIGN KEY (pokemon_id) REFERENCES pokemon(id) ON DELETE CASCADE
                )
            """);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar tabelas do banco.", e);
        }
    }
}