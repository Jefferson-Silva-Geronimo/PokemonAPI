package repository;

import model.Ability;
import model.LearnedMove;
import model.Move;
import model.Pokemon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PokemonRepository {

    public boolean existe(int id) {
        try (Connection conn = DatabaseConnection.get();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM pokemon WHERE id = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar Pokémon no banco.", e);
        }
    }

    public int contarPokemons() {
        try (Connection conn = DatabaseConnection.get();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) AS total FROM pokemon");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

            return 0;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar Pokémons.", e);
        }
    }

    public void salvar(Pokemon pokemon) {
        try (Connection conn = DatabaseConnection.get()) {
            conn.setAutoCommit(false);

            try {
                if (existe(pokemon.getApiId())) {
                    conn.rollback();
                    return;
                }

                salvarPokemon(conn, pokemon);
                salvarAbilities(conn, pokemon);
                salvarMoves(conn, pokemon);
                salvarEvolucao(conn, pokemon);

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar Pokémon no banco.", e);
        }
    }

    private void salvarPokemon(Connection conn, Pokemon pokemon) throws Exception {
        String sql = """
            INSERT INTO pokemon (id, nome, hp, attack, basico)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pokemon.getApiId());
            ps.setString(2, pokemon.getNome());
            ps.setInt(3, pokemon.getMaxHp());
            ps.setInt(4, pokemon.getAttack());
            ps.setBoolean(5, pokemon.isBasico());
            ps.executeUpdate();
        }
    }

    private void salvarAbilities(Connection conn, Pokemon pokemon) throws Exception {
        String sql = """
            INSERT INTO pokemon_ability (pokemon_id, nome, escondida)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Ability ability : pokemon.getAbilities()) {
                ps.setInt(1, pokemon.getApiId());
                ps.setString(2, ability.getNome());
                ps.setBoolean(3, ability.isEscondida());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void salvarMoves(Connection conn, Pokemon pokemon) throws Exception {
        String sql = """
            INSERT INTO pokemon_move
            (pokemon_id, nome, pp_max, tipo, precisao, poder, level_learned_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (LearnedMove learnedMove : pokemon.getLearnedMoves()) {
                Move move = learnedMove.getMove();

                ps.setInt(1, pokemon.getApiId());
                ps.setString(2, move.getNome());
                ps.setInt(3, move.getPpMax());
                ps.setString(4, move.getTipo());
                ps.setInt(5, move.getPrecisao());
                ps.setInt(6, move.getPoder());
                ps.setInt(7, learnedMove.getLevelLearnedAt());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    private void salvarEvolucao(Connection conn, Pokemon pokemon) throws Exception {
        if (!pokemon.temEvolucao()) {
            return;
        }

        String sql = """
            INSERT INTO pokemon_evolution
            (pokemon_id, evolution_name, min_level, hp, attack)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pokemon.getApiId());
            ps.setString(2, pokemon.getEvolutionName());
            ps.setInt(3, pokemon.getEvolutionMinLevel());
            ps.setInt(4, pokemon.getEvolutionHp());
            ps.setInt(5, pokemon.getEvolutionAttack());
            ps.executeUpdate();
        }
    }

    public Pokemon buscarPorId(int id) {
        try (Connection conn = DatabaseConnection.get()) {
            String sql = "SELECT * FROM pokemon WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    Pokemon pokemon = new Pokemon(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getInt("hp"),
                            rs.getInt("attack")
                    );

                    pokemon.setBasico(rs.getBoolean("basico"));

                    carregarAbilities(conn, pokemon);
                    carregarLearnedMoves(conn, pokemon);
                    carregarEvolucao(conn, pokemon);

                    return pokemon;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar Pokémon no banco.", e);
        }
    }

    private void carregarAbilities(Connection conn, Pokemon pokemon) throws Exception {
        String sql = "SELECT * FROM pokemon_ability WHERE pokemon_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pokemon.getApiId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pokemon.adicionarAbility(new Ability(
                            rs.getString("nome"),
                            rs.getBoolean("escondida")
                    ));
                }
            }
        }
    }

    private void carregarLearnedMoves(Connection conn, Pokemon pokemon) throws Exception {
        String sql = """
            SELECT *
            FROM pokemon_move
            WHERE pokemon_id = ?
            ORDER BY level_learned_at ASC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pokemon.getApiId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Move move = new Move(
                            rs.getString("nome"),
                            rs.getInt("pp_max"),
                            rs.getString("tipo"),
                            rs.getInt("precisao"),
                            rs.getInt("poder")
                    );

                    pokemon.adicionarLearnedMove(new LearnedMove(
                            move,
                            rs.getInt("level_learned_at")
                    ));
                }
            }
        }
    }

    private void carregarEvolucao(Connection conn, Pokemon pokemon) throws Exception {
        String sql = "SELECT * FROM pokemon_evolution WHERE pokemon_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pokemon.getApiId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pokemon.configurarEvolucao(
                            rs.getString("evolution_name"),
                            rs.getInt("min_level"),
                            rs.getInt("hp"),
                            rs.getInt("attack")
                    );
                }
            }
        }
    }

    public List<Integer> listarIdsBasicos() {
        List<Integer> ids = new ArrayList<>();

        String sql = """
            SELECT id
            FROM pokemon
            WHERE basico = TRUE
            ORDER BY id
        """;

        try (Connection conn = DatabaseConnection.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }

            return ids;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar Pokémons básicos.", e);
        }
    }
}