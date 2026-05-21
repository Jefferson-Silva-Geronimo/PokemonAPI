package game;

import Util.InputUtil;
import Util.RandomUtil;
import model.Player;
import model.Pokemon;
import repository.SaveRepository;
import service.PokemonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    private final Scanner sc;
    private final PokemonService pokemonService;
    private final SaveRepository saveRepository;

    public Game() {
        this.sc = new Scanner(System.in);
        this.pokemonService = new PokemonService();
        this.saveRepository = new SaveRepository();
    }

    public void start() {
        pokemonService.carregarBaseInicial();

        System.out.println("=================================");
        System.out.println("        POKÉMON CONSOLE RPG      ");
        System.out.println("=================================");

        boolean executando = true;

        while (executando) {
            System.out.println("\nMenu principal:");
            System.out.println("1 - Novo jogo");
            System.out.println("2 - Carregar jogo");
            System.out.println("3 - Sair");

            int opcao = InputUtil.lerInt(sc, "Escolha uma opção: ");

            switch (opcao) {
                case 1 -> {
                    Player novoJogador = iniciarNovoJogo();
                    iniciarJornada(novoJogador);
                }
                case 2 -> {
                    Player jogadorCarregado = saveRepository.carregar();

                    if (jogadorCarregado != null) {
                        iniciarJornada(jogadorCarregado);
                    }
                }
                case 3 -> {
                    executando = false;
                    System.out.println("Obrigado por jogar!");
                }
                default -> System.out.println("Opção inválida. Escolha 1, 2 ou 3.");
            }
        }
    }

    private Player iniciarNovoJogo() {
        String nomeJogador = InputUtil.lerTexto(sc, "\nDigite seu nome: ");

        List<Pokemon> opcoes = sortearPokemonsIniciais();

        System.out.println("\nEscolha seu Pokémon inicial:");

        for (int i = 0; i < opcoes.size(); i++) {
            System.out.println("\nOpção " + (i + 1) + ":");
            opcoes.get(i).mostrarStatus();
        }

        int escolha;

        do {
            escolha = InputUtil.lerInt(sc, "Digite o número do Pokémon escolhido: ");

            if (escolha < 1 || escolha > opcoes.size()) {
                System.out.println("Escolha inválida. Digite 1, 2 ou 3.");
            }

        } while (escolha < 1 || escolha > opcoes.size());

        Pokemon pokemonEscolhido = opcoes.get(escolha - 1);

        System.out.println("\nVocê escolheu " + pokemonEscolhido.getNome() + "!");

        return new Player(nomeJogador, pokemonEscolhido);
    }

    private List<Pokemon> sortearPokemonsIniciais() {
        List<Integer> idsBasicos = pokemonService.listarIdsBasicos();

        if (idsBasicos.size() < 3) {
            throw new RuntimeException("Não há Pokémons básicos suficientes no banco.");
        }

        List<Integer> indices = RandomUtil.sortearNumerosDistintos(3, 0, idsBasicos.size() - 1);
        List<Pokemon> pokemons = new ArrayList<>();

        for (Integer indice : indices) {
            pokemons.add(pokemonService.buscarPokemon(idsBasicos.get(indice)));
        }

        return pokemons;
    }

    private void iniciarJornada(Player player) {
        boolean emJornada = true;

        while (emJornada && player.getPokemon().vivo()) {
            System.out.println("\n=================================");
            System.out.println("Jornada de " + player.getNome());
            System.out.println("Pokémon atual: " + player.getPokemon().getNome());
            System.out.println("Nível: " + player.getPokemon().getLevel());
            System.out.println("Poções: " + player.getPotions());
            System.out.println("=================================");

            System.out.println("1 - Enfrentar próximo inimigo");
            System.out.println("2 - Ver status do Pokémon");
            System.out.println("3 - Salvar jogo");
            System.out.println("4 - Sair para o menu principal");

            int opcao = InputUtil.lerInt(sc, "Escolha uma opção: ");

            switch (opcao) {
                case 1 -> {
                    Pokemon inimigo = gerarInimigo(player.getPokemon());
                    Battle battle = new Battle(player, inimigo);
                    battle.start();
                }
                case 2 -> player.getPokemon().mostrarStatus();
                case 3 -> saveRepository.salvar(player);
                case 4 -> emJornada = false;
                default -> System.out.println("Opção inválida.");
            }
        }

        if (!player.getPokemon().vivo()) {
            System.out.println("\nSua jornada terminou porque seu Pokémon foi derrotado.");
            System.out.println("Você pode carregar um save anterior ou iniciar um novo jogo.");
        }
    }

    private Pokemon gerarInimigo(Pokemon pokemonJogador) {
        int nivelJogador = pokemonJogador.getLevel();
        int variacao = RandomUtil.sortearNumero(0, 2) - 1;
        int nivelInimigo = Math.max(1, nivelJogador + variacao);

        int id = RandomUtil.sortearNumero(1, 150);

        Pokemon inimigo = pokemonService.buscarPokemonComNivel(id, nivelInimigo);

        System.out.println("\nUm " + inimigo.getNome() + " selvagem apareceu!");
        System.out.println("Nível do inimigo: " + inimigo.getLevel());

        return inimigo;
    }
}