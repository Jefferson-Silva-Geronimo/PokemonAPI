package repository;

import model.Move;
import model.Player;
import model.Pokemon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SaveRepository {

    private final PokemonRepository pokemonRepository;

    public SaveRepository() {
        this.pokemonRepository = new PokemonRepository();
    }

    public void salvar(Player player) {
        Pokemon pokemon = player.getPokemon();

        try (Connection conn = DatabaseConnection.get()) {
            conn.setAutoCommit(false);

            try {
                limparSaveAnterior(conn);

                int saveId = salvarDadosPrincipais(conn, player, pokemon);
                salvarMovimentos(conn, saveId, pokemon);

                conn.commit();

                System.out.println("Jogo salvo com sucesso!");

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar jogo.", e);
        }
    }

    private void limparSaveAnterior(Connection conn) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM save_game")) {
            ps.executeUpdate();
        }
    }

    private int salvarDadosPrincipais(Connection conn, Player player, Pokemon pokemon) throws Exception {
        String sql = """
            INSERT INTO save_game
            (nome_jogador, pokemon_id, nome_pokemon, hp, max_hp, attack, level, xp, potions, evoluiu)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, player.getNome());
            ps.setInt(2, pokemon.getApiId());
            ps.setString(3, pokemon.getNome());
            ps.setInt(4, pokemon.getHp());
            ps.setInt(5, pokemon.getMaxHp());
            ps.setInt(6, pokemon.getAttack());
            ps.setInt(7, pokemon.getLevel());
            ps.setInt(8, pokemon.getXp());
            ps.setInt(9, player.getPotions());
            ps.setBoolean(10, pokemon.isEvoluiu());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new RuntimeException("Não foi possível obter o ID do save.");
    }

    private void salvarMovimentos(Connection conn, int saveId, Pokemon pokemon) throws Exception {
        String sql = """
            INSERT INTO save_move
            (save_id, nome, pp_atual, pp_max, tipo, precisao, poder)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Move move : pokemon.getMoves()) {
                ps.setInt(1, saveId);
                ps.setString(2, move.getNome());
                ps.setInt(3, move.getPpAtual());
                ps.setInt(4, move.getPpMax());
                ps.setString(5, move.getTipo());
                ps.setInt(6, move.getPrecisao());
                ps.setInt(7, move.getPoder());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    public Player carregar() {
        try (Connection conn = DatabaseConnection.get();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("""
                 SELECT *
                 FROM save_game
                 ORDER BY id DESC
                 LIMIT 1
             """)) {

            if (!rs.next()) {
                System.out.println("Nenhum jogo salvo encontrado.");
                return null;
            }

            int saveId = rs.getInt("id");
            int pokemonId = rs.getInt("pokemon_id");

            Pokemon pokemon = pokemonRepository.buscarPorId(pokemonId);

            if (pokemon == null) {
                System.out.println("Pokémon do save não encontrado na base local.");
                return null;
            }

            pokemon.setNome(rs.getString("nome_pokemon"));
            pokemon.setMaxHp(rs.getInt("max_hp"));
            pokemon.setHp(rs.getInt("hp"));
            pokemon.setAttack(rs.getInt("attack"));
            pokemon.setLevel(rs.getInt("level"));
            pokemon.setXp(rs.getInt("xp"));
            pokemon.setEvoluiu(rs.getBoolean("evoluiu"));

            pokemon.limparMovimentosAtuais();
            carregarMovimentos(conn, saveId, pokemon);

            Player player = new Player(rs.getString("nome_jogador"), pokemon);
            player.setPotions(rs.getInt("potions"));

            System.out.println("Jogo carregado com sucesso!");

            return player;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar jogo.", e);
        }
    }

    private void carregarMovimentos(Connection conn, int saveId, Pokemon pokemon) throws Exception {
        String sql = """
            SELECT *
            FROM save_move
            WHERE save_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saveId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Move move = new Move(
                            rs.getString("nome"),
                            rs.getInt("pp_max"),
                            rs.getString("tipo"),
                            rs.getInt("precisao"),
                            rs.getInt("poder")
                    );

                    move.setPpAtual(rs.getInt("pp_atual"));
                    pokemon.adicionarMovimentoAtual(move);
                }
            }
        }
    }
}