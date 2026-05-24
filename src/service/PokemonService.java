package service;

import api.PokemonApi;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Ability;
import model.LearnedMove;
import model.Move;
import model.Pokemon;
import repository.PokemonRepository;

import java.util.List;

/**
 * Classe responsável pela lógica de negócio dos Pokémons.
 *
 * Atua como intermediário entre a API externa e o banco de dados,
 * sendo responsável por carregar, processar e fornecer Pokémons
 * prontos para uso no jogo.
 *
 * Objetivo:
 * - Buscar dados da API
 * - Processar e montar objetos Pokémon
 * - Persistir dados no banco
 *
 * Conceitos aplicados:
 * - Camada de serviço
 * - Separação de responsabilidades
 */
public class PokemonService {

    private final PokemonApi api;
    private final PokemonRepository repository;
    private final Gson gson;

    public PokemonService() {
        this.api = new PokemonApi();
        this.repository = new PokemonRepository();
        this.gson = new Gson();
    }

    public void carregarBaseInicial() {
        if (repository.contarPokemons() >= 150) {
            System.out.println("Base local de Pokémons já carregada.");
            return;
        }

        System.out.println("Carregando 150 Pokémons da PokéAPI para o banco local.");

        for (int id = 1; id <= 150; id++) {
            if (!repository.existe(id)) {
                Pokemon pokemon = buscarPokemonCompletoDaApi(id);
                repository.salvar(pokemon);
                System.out.println("Salvo no banco: " + id + " - " + pokemon.getNome());
            }
        }

        System.out.println("Base local carregada com sucesso.");
    }

    public Pokemon buscarPokemon(int id) {
        Pokemon pokemon = repository.buscarPorId(id);

        if (pokemon == null) {
            throw new RuntimeException("Pokémon não encontrado no banco local: " + id);
        }

        pokemon.setTipo(api.getPokemonTypes(id));

        pokemon.prepararParaNivelInicial(1);
        return pokemon;
    }

    public Pokemon buscarPokemonComNivel(int id, int nivel) {
        Pokemon pokemon = buscarPokemon(id);

        int nivelSeguro = Math.max(1, nivel);
        int hpEscalado = pokemon.getMaxHp() + ((nivelSeguro - 1) * 10);
        int attackEscalado = pokemon.getAttack() + ((nivelSeguro - 1) * 5);

        pokemon.setMaxHp(hpEscalado);
        pokemon.setAttack(attackEscalado);
        pokemon.prepararParaNivelInicial(nivelSeguro);

        return pokemon;
    }

    public List<Integer> listarIdsBasicos() {
        return repository.listarIdsBasicos();
    }

    private Pokemon buscarPokemonCompletoDaApi(int id) {
        String json = api.getPokemon(id);
        JsonObject obj = gson.fromJson(json, JsonObject.class);

        String nome = formatarNome(obj.get("name").getAsString());
        JsonArray stats = obj.getAsJsonArray("stats");

        int hp = stats.get(0).getAsJsonObject().get("base_stat").getAsInt();
        int attack = stats.get(1).getAsJsonObject().get("base_stat").getAsInt();

        Pokemon pokemon = new Pokemon(id, nome, hp, attack);
        List<String> tipo = api.getPokemonTypes(id);
        pokemon.setTipo(tipo);

        carregarAbilities(obj, pokemon);
        carregarMoves(obj, pokemon);
        carregarDadosDeEvolucao(obj, pokemon);

        return pokemon;
    }

    private void carregarAbilities(JsonObject obj, Pokemon pokemon) {
        JsonArray abilities = obj.getAsJsonArray("abilities");

        for (int i = 0; i < abilities.size(); i++) {
            JsonObject abilityObj = abilities.get(i).getAsJsonObject();

            String nome = abilityObj
                    .getAsJsonObject("ability")
                    .get("name")
                    .getAsString();

            boolean escondida = abilityObj
                    .get("is_hidden")
                    .getAsBoolean();

            pokemon.adicionarAbility(new Ability(formatarNome(nome), escondida));
        }
    }

    private void carregarMoves(JsonObject obj, Pokemon pokemon) {
        JsonArray moves = obj.getAsJsonArray("moves");

        for (int i = 0; i < moves.size(); i++) {
            JsonObject moveObj = moves.get(i).getAsJsonObject();

            int levelLearnedAt = extrairMenorNivelAprendizado(moveObj);

            if (levelLearnedAt <= 0) {
                continue;
            }

            String url = moveObj
                    .getAsJsonObject("move")
                    .get("url")
                    .getAsString();

            Move move = buscarMoveDaApi(url);

            if (move != null) {
                pokemon.adicionarLearnedMove(new LearnedMove(move, levelLearnedAt));
            }
        }
    }

    private int extrairMenorNivelAprendizado(JsonObject moveObj) {
        JsonArray details = moveObj.getAsJsonArray("version_group_details");

        int menorNivel = Integer.MAX_VALUE;

        for (int i = 0; i < details.size(); i++) {
            JsonObject detail = details.get(i).getAsJsonObject();

            String metodo = detail
                    .getAsJsonObject("move_learn_method")
                    .get("name")
                    .getAsString();

            int nivel = detail
                    .get("level_learned_at")
                    .getAsInt();

            if ("level-up".equalsIgnoreCase(metodo) && nivel > 0 && nivel < menorNivel) {
                menorNivel = nivel;
            }
        }

        if (menorNivel == Integer.MAX_VALUE) {
            return 0;
        }

        return menorNivel;
    }

    private Move buscarMoveDaApi(String url) {
        try {
            String json = api.get(url);
            JsonObject obj = gson.fromJson(json, JsonObject.class);

            String nome = formatarNome(obj.get("name").getAsString());
            int pp = valorOuPadrao(obj, "pp", 1);
            int precisao = valorOuPadrao(obj, "accuracy", 100);
            int poder = valorOuPadrao(obj, "power", 0);

            String tipo = obj
                    .getAsJsonObject("type")
                    .get("name")
                    .getAsString();

            return new Move(nome, pp, tipo, precisao, poder);

        } catch (Exception e) {
            return null;
        }
    }

    private void carregarDadosDeEvolucao(JsonObject pokemonObj, Pokemon pokemon) {
        try {
            String speciesUrl = pokemonObj
                    .getAsJsonObject("species")
                    .get("url")
                    .getAsString();

            String speciesJson = api.get(speciesUrl);
            JsonObject speciesObj = gson.fromJson(speciesJson, JsonObject.class);

            String evolutionChainUrl = speciesObj
                    .getAsJsonObject("evolution_chain")
                    .get("url")
                    .getAsString();

            String evolutionJson = api.get(evolutionChainUrl);
            JsonObject evolutionObj = gson.fromJson(evolutionJson, JsonObject.class);

            JsonObject chain = evolutionObj.getAsJsonObject("chain");

            String nomeRaiz = chain
                    .getAsJsonObject("species")
                    .get("name")
                    .getAsString();

            pokemon.setBasico(formatarNome(nomeRaiz).equalsIgnoreCase(pokemon.getNome()));

            configurarProximaEvolucao(chain, pokemon);

        } catch (Exception e) {
            pokemon.setBasico(true);
        }
    }

    private boolean configurarProximaEvolucao(JsonObject chain, Pokemon pokemon) {
        String speciesName = chain
                .getAsJsonObject("species")
                .get("name")
                .getAsString();

        JsonArray evolvesTo = chain.getAsJsonArray("evolves_to");

        if (formatarNome(speciesName).equalsIgnoreCase(pokemon.getNome())) {
            if (evolvesTo == null || evolvesTo.isEmpty()) {
                return true;
            }

            JsonObject evolutionObj = evolvesTo.get(0).getAsJsonObject();

            String evolutionName = evolutionObj
                    .getAsJsonObject("species")
                    .get("name")
                    .getAsString();

            int minLevel = extrairNivelMinimoEvolucao(evolutionObj);

            if (minLevel <= 0) {
                minLevel = 5;
            }

            configurarStatsEvolucao(pokemon, evolutionName, minLevel);
            return true;
        }

        if (evolvesTo != null) {
            for (int i = 0; i < evolvesTo.size(); i++) {
                if (configurarProximaEvolucao(evolvesTo.get(i).getAsJsonObject(), pokemon)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int extrairNivelMinimoEvolucao(JsonObject evolutionObj) {
        JsonArray details = evolutionObj.getAsJsonArray("evolution_details");

        if (details == null || details.isEmpty()) {
            return 0;
        }

        JsonObject detail = details.get(0).getAsJsonObject();

        if (!detail.has("min_level") || detail.get("min_level").isJsonNull()) {
            return 0;
        }

        return detail.get("min_level").getAsInt();
    }

    private void configurarStatsEvolucao(Pokemon pokemon, String evolutionName, int minLevel) {
        try {
            String json = api.getPokemon(evolutionName);
            JsonObject obj = gson.fromJson(json, JsonObject.class);

            JsonArray stats = obj.getAsJsonArray("stats");

            int hp = stats.get(0).getAsJsonObject().get("base_stat").getAsInt();
            int attack = stats.get(1).getAsJsonObject().get("base_stat").getAsInt();

            pokemon.configurarEvolucao(formatarNome(evolutionName), minLevel, hp, attack);

        } catch (Exception e) {
            pokemon.configurarEvolucao(
                    formatarNome(evolutionName),
                    minLevel,
                    pokemon.getMaxHp() + 20,
                    pokemon.getAttack() + 10
            );
        }
    }

    private int valorOuPadrao(JsonObject obj, String campo, int padrao) {
        if (!obj.has(campo) || obj.get(campo).isJsonNull()) {
            return padrao;
        }

        return obj.get(campo).getAsInt();
    }

    private String formatarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "Desconhecido";
        }

        String nomeLimpo = nome.trim().toLowerCase().replace("-", " ");

        return nomeLimpo.substring(0, 1).toUpperCase() + nomeLimpo.substring(1);
    }
}